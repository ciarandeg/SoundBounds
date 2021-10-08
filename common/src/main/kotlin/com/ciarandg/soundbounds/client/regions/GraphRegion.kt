package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import com.ciarandg.soundbounds.client.render.toBox
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import java.lang.IllegalStateException

@Suppress("UnstableApiUsage")
class GraphRegion(private val blocks: Set<BlockPos>) {
    // The wireframe and face outline operate on the same principle: within a given selection,
    // any face/edge that ought to be shown as part of an outline will be unique to the block that
    // contains it.
    fun getWireframe(): Set<Pair<Vec3i, Vec3i>> {
        val unique = keepUnique(blocks.map { getBlockEdgesMemoized(it) })
        val pairs = unique.map { Pair(it.vertex1, it.vertex2) }
        return pairs.toSet()
    }
    fun getFaceOutline(): Set<Pair<List<Vector3f>, Direction>> {
        val unique = keepUnique(blocks.map { getBlockFaces(it) })
        val pairs = unique.map { Pair(nudgeFace(it), it.facing) }
        return pairs.toSet()
    }

    private fun <T> keepUnique(setList: List<Set<T>>): Set<T> {
        val counts = HashMap<T, Int>()
        setList.forEach { set ->
            set.forEach { item ->
                val currentCount = counts.getOrDefault(item, 0)
                counts[item] = currentCount + 1
            }
        }
        val unique = counts.entries.mapNotNull {
            if (it.value == 1) it.key else null
        }
        return unique.toSet()
    }

    private fun nudgeFace(face: Face3i): List<Vector3f> {
        fun toFloatVec(intVec: Vec3i) = Vector3f(intVec.x.toFloat(), intVec.y.toFloat(), intVec.z.toFloat())

        val nudgeInc = Z_INCREMENT.toFloat()
        val nudgeVec = with(toFloatVec(face.facing.vector)) { Vector3f(x * nudgeInc, y * nudgeInc, z * nudgeInc) }
        val out = listOf(
            toFloatVec(face.bottomLeft),
            toFloatVec(face.topLeft),
            toFloatVec(face.topRight),
            toFloatVec(face.bottomRight)
        )
        out.forEach { it.add(nudgeVec) }
        return out
    }

    private fun getBlockEdgesMemoized(block: BlockPos) =
        Memoizer.blockEdgesCache.getOrPut(block) { getBlockEdges(block) }

    private fun getBlockEdges(block: BlockPos): Set<Edge3i> {
        // map a block to its 12 corresponding physical edges
        val corners = getBlockCornerPositions(block)
        val edges = corners.flatMapIndexed { index, c ->
            corners.subList(index + 1, corners.size).mapNotNull {
                // corners are only adjacent if their positions are identical in 2 dimensions
                if (
                    (c.x == it.x && c.y == it.y) ||
                    (c.x == it.x && c.z == it.z) ||
                    (c.y == it.y && c.z == it.z)
                ) Edge3i(c, it)
                else null
            }
        }.toSet()
        if (edges.size != 12) throw IllegalStateException("A cube has 12 edges, not ${edges.size}")
        return edges
    }

    private fun getBlockFaces(block: BlockPos): Set<Face3i> {
        // map a block to its 6 corresponding faces
        val faces = with(block.toBox()) {
            setOf(
                Face3i(Vec3i(maxX, minY, minZ), Vec3i(maxX, maxY, minZ), Vec3i(minX, maxY, minZ), Vec3i(minX, minY, minZ), Direction.NORTH),
                Face3i(Vec3i(minX, minY, minZ), Vec3i(minX, minY, maxZ), Vec3i(minX, maxY, maxZ), Vec3i(minX, maxY, minZ), Direction.WEST),
                Face3i(Vec3i(minX, minY, minZ), Vec3i(maxX, minY, minZ), Vec3i(maxX, minY, maxZ), Vec3i(minX, minY, maxZ), Direction.DOWN),
                Face3i(Vec3i(minX, minY, maxZ), Vec3i(maxX, minY, maxZ), Vec3i(maxX, maxY, maxZ), Vec3i(minX, maxY, maxZ), Direction.SOUTH),
                Face3i(Vec3i(maxX, minY, maxZ), Vec3i(maxX, minY, minZ), Vec3i(maxX, maxY, minZ), Vec3i(maxX, maxY, maxZ), Direction.EAST),
                Face3i(Vec3i(minX, maxY, maxZ), Vec3i(minX, maxY, minZ), Vec3i(maxX, maxY, minZ), Vec3i(maxX, maxY, maxZ), Direction.UP)
            )
        }
        if (faces.size != 6) throw IllegalStateException("A cube has 6 faces, not ${faces.size}")
        return faces
    }

    private fun getBlockCornerPositions(block: BlockPos): List<Vec3i> {
        val box = block.toBox()
        return listOf(
            Vec3i(box.minX, box.minY, box.minZ),
            Vec3i(box.maxX, box.minY, box.minZ),
            Vec3i(box.minX, box.maxY, box.minZ),
            Vec3i(box.minX, box.minY, box.maxZ),
            Vec3i(box.maxX, box.maxY, box.minZ),
            Vec3i(box.minX, box.maxY, box.maxZ),
            Vec3i(box.maxX, box.minY, box.maxZ),
            Vec3i(box.maxX, box.maxY, box.maxZ)
        )
    }

    private class Edge3i(val vertex1: Vec3i, val vertex2: Vec3i) {
        private val pair1 = Pair(vertex1, vertex2)
        private val pair2 = Pair(vertex2, vertex1)

        override fun equals(other: Any?): Boolean {
            if (other !is Edge3i) return false
            val otherPair = Pair(other.vertex1, other.vertex2)
            return pair1 == otherPair || pair2 == otherPair
        }
        override fun hashCode() = 31 * (pair1.hashCode() + pair2.hashCode())
    }

    private data class Face3i(
        val bottomLeft: Vec3i,
        val topLeft: Vec3i,
        val topRight: Vec3i,
        val bottomRight: Vec3i,
        val facing: Direction
    ) {
        init {
            with(cornerSet()) { if (size != 4) throw IllegalStateException("Face must have 4 unique corners, not $size") }
        }

        fun cornerSet() = setOf(bottomLeft, topLeft, topRight, bottomRight)

        // equals and hashcode ignore the face's direction for my personal convenience :^)
        override fun equals(other: Any?): Boolean {
            if (other !is Face3i) return false
            return cornerSet() == other.cornerSet()
        }
        override fun hashCode() = cornerSet().hashCode()
    }

    private object Memoizer {
        val blockEdgesCache: MutableMap<BlockPos, Set<Edge3i>> = HashMap()
    }
}
