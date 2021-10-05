package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.render.toBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import java.lang.IllegalStateException

@Suppress("UnstableApiUsage")
class GraphRegion(private val blocks: Set<BlockPos>) {
    fun getWireframe(): Set<Pair<Vec3i, Vec3i>> {
        val blocksWithEdges = blocks.associateWith { getBlockPhysicalEdges(it) }

        // Edges on the perimeter are unique to their corresponding block within the region,
        // so I count the instances of each edge and ditch anything with more than one instance
        val lines = HashMap<Line3i, Int>()
        for (entry in blocksWithEdges.entries) {
            entry.value.forEach { newLine ->
                val currentCount = lines.getOrDefault(newLine, 0)
                lines[newLine] = currentCount + 1
            }
        }
        val filtered = lines.entries.mapNotNull { if (it.value == 1) Pair(it.key.vertex1, it.key.vertex2) else null }
        return filtered.toSet()
    }

    private fun getBlockPhysicalEdges(block: BlockPos): Set<Line3i> {
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
}
