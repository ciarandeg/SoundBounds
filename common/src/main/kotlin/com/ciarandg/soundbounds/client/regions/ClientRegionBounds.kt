package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import com.ciarandg.soundbounds.client.render.toBox
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import java.lang.IllegalStateException

class ClientRegionBounds(val blockSet: Set<BlockPos>, val focals: Set<BlockPos> = setOf()) {
    // The wireframes and face outline operate on the same principle: within a given selection,
    // any face/edge that ought to be shown as part of an outline will be unique to the block that
    // contains it.
    fun getWireframe(): Set<Pair<Vec3i, Vec3i>> {
        val unique = keepUnique(blockSet.map { getBlockEdges(it) })
        val pairs = unique.map { Pair(it.vertex1, it.vertex2) }
        return pairs.toSet()
    }

    fun getFocalWireframe(): Set<Set<Pair<Vec3i, Vec3i>>> =
        focals.map { focal -> getBlockEdges(focal).map { edge -> Pair(edge.vertex1, edge.vertex2) }.toSet() }.toSet()

    fun getFaceOutline(): Set<Pair<List<Vector3f>, Direction>> {
        val unique = keepUnique(blockSet.map { getBlockFaces(it) })
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

    private fun getBlockEdges(block: BlockPos): Set<Edge3i> {
        val edges = with(block.toBox()) {
            setOf(
                Edge3i(Vec3i(minX, minY, minZ), Vec3i(maxX, minY, minZ)),
                Edge3i(Vec3i(minX, minY, minZ), Vec3i(minX, maxY, minZ)),
                Edge3i(Vec3i(minX, minY, minZ), Vec3i(minX, minY, maxZ)),
                Edge3i(Vec3i(maxX, minY, minZ), Vec3i(maxX, maxY, minZ)),
                Edge3i(Vec3i(maxX, minY, minZ), Vec3i(maxX, minY, maxZ)),
                Edge3i(Vec3i(minX, maxY, minZ), Vec3i(maxX, maxY, minZ)),
                Edge3i(Vec3i(minX, maxY, minZ), Vec3i(minX, maxY, maxZ)),
                Edge3i(Vec3i(maxX, maxY, minZ), Vec3i(maxX, maxY, maxZ)),
                Edge3i(Vec3i(maxX, maxY, maxZ), Vec3i(maxX, minY, maxZ)),
                Edge3i(Vec3i(maxX, maxY, maxZ), Vec3i(minX, maxY, maxZ)),
                Edge3i(Vec3i(maxX, minY, maxZ), Vec3i(minX, minY, maxZ)),
                Edge3i(Vec3i(minX, minY, maxZ), Vec3i(minX, maxY, maxZ)),
            )
        }
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

    companion object {
        fun fromBoxCorners(corner1: BlockPos?, corner2: BlockPos?, makeCornersFocals: Boolean): ClientRegionBounds? {
            fun blockifyBox(box: Box): Set<BlockPos> {
                val set: MutableSet<BlockPos> = HashSet()
                val minVec = Vec3i(box.minX, box.minY, box.minZ)
                val maxVec = Vec3i(box.maxX - 1, box.maxY - 1, box.maxZ - 1)
                for (x in minVec.x..maxVec.x) {
                    for (y in minVec.y..maxVec.y) {
                        for (z in minVec.z..maxVec.z) {
                            set.add(BlockPos(x, y, z))
                        }
                    }
                }
                return set
            }

            return when {
                corner1 == null && corner2 == null -> null
                corner1 != null && corner2 == null -> {
                    val block = blockifyBox(corner1.toBox())
                    if (makeCornersFocals) ClientRegionBounds(block, setOf(corner1)) else ClientRegionBounds(block)
                }
                corner1 == null && corner2 != null -> {
                    val block = blockifyBox(corner2.toBox())
                    if (makeCornersFocals) ClientRegionBounds(block, setOf(corner2)) else ClientRegionBounds(block)
                }
                corner1 != null && corner2 != null -> {
                    val unionBox = blockifyBox(corner1.toBox().union(corner2.toBox()))
                    if (makeCornersFocals) ClientRegionBounds(unionBox, setOf(corner1, corner2)) else ClientRegionBounds(unionBox)
                }
                else -> throw IllegalStateException("Tautologically impossible")
            }
        }
    }
}
