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
    fun getWireframe(): Set<Pair<Vec3i, Vec3i>> {
        // Edges on the perimeter are unique to their corresponding block within the region,
        // so I count the instances of each edge and ditch anything with more than one instance
        val lineCounts = HashMap<Line3i, Int>()
        blocks.map { getBlockEdgesMemoized(it) }.forEach { edgeSet ->
            edgeSet.forEach { newLine ->
                val currentCount = lineCounts.getOrDefault(newLine, 0)
                lineCounts[newLine] = currentCount + 1
            }
        }
        val filtered = lineCounts.entries.mapNotNull { if (it.value == 1) Pair(it.key.vertex1, it.key.vertex2) else null }
        return filtered.toSet()
    }

    fun getFaceOutline(): Set<Pair<List<Vector3f>, Direction>> {
        // Faces on the perimeter are unique to their corresponding block within the region,
        // so I count the instances of each face and ditch anything with more than one instance
        val faceCounts = HashMap<Face3i, Int>()
        blocks.map { getBlockFaces(it) }.forEach { faceSet ->
            faceSet.forEach { newFace ->
                val currentCount = faceCounts.getOrDefault(newFace, 0)
                faceCounts[newFace] = currentCount + 1
            }
        }
        val filtered = faceCounts.entries.mapNotNull {
            if (it.value == 1) {
                val nudged = nudgeFace(it.key)
                Pair(nudged, it.key.facing)
            } else null
        }
        return filtered.toSet()
    }

    private fun nudgeFace(face: Face3i): List<Vector3f> {
        fun toFloatVec(intVec: Vec3i) = Vector3f(intVec.x.toFloat(), intVec.y.toFloat(), intVec.z.toFloat())

        val nudgeInc = Z_INCREMENT.toFloat()
        val nudgeVec = with(toFloatVec(face.facing.vector)) { Vector3f(x * nudgeInc, y * nudgeInc, z * nudgeInc) }
        val out = listOf(
            toFloatVec(face.corner1),
            toFloatVec(face.corner2),
            toFloatVec(face.corner3),
            toFloatVec(face.corner4)
        )
        out.forEach { it.add(nudgeVec) }
        return out
        // val nudgeVec = with(face.facing.vector) { Vector3f(x.toFloat() * nudgeInc, y.toFloat() * nudgeInc, z.toFloat() * nudgeInc) }
    }

    private fun getBlockEdgesMemoized(block: BlockPos) =
        Memoizer.blockEdgesCache.getOrPut(block) { getBlockEdges(block) }

    private fun getBlockFaces(block: BlockPos): Set<Face3i> {
        // map a block to its 6 corresponding faces
        val faces = with(block.toBox()) {
            setOf(
                Face3i(Vec3i(minX, minY, minZ), Vec3i(maxX, minY, minZ), Vec3i(maxX, maxY, minZ), Vec3i(minX, maxY, minZ), Direction.NORTH),
                Face3i(Vec3i(minX, minY, minZ), Vec3i(minX, minY, maxZ), Vec3i(minX, maxY, maxZ), Vec3i(minX, maxY, minZ), Direction.WEST),
                Face3i(Vec3i(minX, minY, minZ), Vec3i(maxX, minY, minZ), Vec3i(maxX, minY, maxZ), Vec3i(minX, minY, maxZ), Direction.UP),
                Face3i(Vec3i(maxX, maxY, maxZ), Vec3i(minX, maxY, maxZ), Vec3i(minX, minY, maxZ), Vec3i(maxX, minY, maxZ), Direction.SOUTH),
                Face3i(Vec3i(maxX, maxY, maxZ), Vec3i(maxX, maxY, minZ), Vec3i(maxX, minY, minZ), Vec3i(maxX, minY, maxZ), Direction.EAST),
                Face3i(Vec3i(maxX, maxY, maxZ), Vec3i(minX, maxY, maxZ), Vec3i(minX, maxY, minZ), Vec3i(maxX, maxY, minZ), Direction.DOWN)
            )
        }
        if (faces.size != 6) throw IllegalStateException("A cube has 6 faces, not ${faces.size}")
        return faces
    }

    private fun getBlockEdges(block: BlockPos): Set<Line3i> {
        // map a block to its 12 corresponding physical edges
        val corners = getBlockCornerPositions(block)
        val edges = corners.flatMapIndexed { index, c ->
            corners.subList(index + 1, corners.size).mapNotNull {
                // corners are only adjacent if their positions are identical in 2 dimensions
                if (
                    (c.x == it.x && c.y == it.y) ||
                    (c.x == it.x && c.z == it.z) ||
                    (c.y == it.y && c.z == it.z)
                ) Line3i(c, it)
                else null
            }
        }.toSet()
        if (edges.size != 12) throw IllegalStateException("A cube has 12 edges, not ${edges.size}")
        return edges
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

    private class Line3i(val vertex1: Vec3i, val vertex2: Vec3i) {
        private val pair1 = Pair(vertex1, vertex2)
        private val pair2 = Pair(vertex2, vertex1)

        override fun equals(other: Any?): Boolean {
            if (other !is Line3i) return false
            val otherPair = Pair(other.vertex1, other.vertex2)
            return pair1 == otherPair || pair2 == otherPair
        }
        override fun hashCode() = 31 * (pair1.hashCode() + pair2.hashCode())
    }

    private data class Face3i(
        val corner1: Vec3i,
        val corner2: Vec3i,
        val corner3: Vec3i,
        val corner4: Vec3i,
        val facing: Direction
    ) {
        init {
            with(cornerSet()) { if (size != 4) throw IllegalStateException("Face must have 4 unique corners, not $size") }
        }

        fun cornerSet() = setOf(corner1, corner2, corner3, corner4)

        // equals and hashcode ignore the face's direction for my personal convenience :^)
        override fun equals(other: Any?): Boolean {
            if (other !is Face3i) return false
            return cornerSet() == other.cornerSet()
        }
        override fun hashCode() = cornerSet().hashCode()
    }

    private object Memoizer {
        val blockEdgesCache: MutableMap<BlockPos, Set<Line3i>> = HashMap()
    }
}
