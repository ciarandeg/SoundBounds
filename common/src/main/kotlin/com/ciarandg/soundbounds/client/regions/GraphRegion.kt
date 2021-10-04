package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.toBox
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import java.lang.IllegalStateException

@Suppress("UnstableApiUsage")
class GraphRegion(blocks: Set<BlockPos>) {
    private val graph: MutableGraph<BlockPos> = GraphBuilder.undirected().allowsSelfLoops(false).build()
    init {
        blocks.forEach {
            graph.addNode(it)
            addAllEdges(it)
        }
    }

    private fun addAllEdges(node: BlockPos) {
        val nodes = graph.nodes()
        if (!nodes.contains(node)) throw IllegalStateException()

        val potentialAdjacencies = listOf(
            BlockPos(node.x + 1, node.y, node.z),
            BlockPos(node.x - 1, node.y, node.z),
            BlockPos(node.x, node.y + 1, node.z),
            BlockPos(node.x, node.y - 1, node.z),
            BlockPos(node.x, node.y, node.z + 1),
            BlockPos(node.x, node.y, node.z - 1)
        )

        for (a in potentialAdjacencies) {
            if (nodes.contains(a)) graph.putEdge(node, a)
        }
    }

    fun getWireframe(): Set<Pair<Vec3i, Vec3i>> {
        // map nodes to their 12 corresponding physical edges, filter out any that are shared by adjacent nodes
        val nodes = graph.nodes()
        val blocksWithEdges = nodes.associateWith { getBlockPhysicalEdges(it) }

        SoundBounds.LOGGER.info("EDGE COUNT WITHOUT CULLING: ${blocksWithEdges.size}")

        // val unfiltered = blocksWithEdges.values.flatten().map { Pair(it.vec1, it.vec2) }
        // return unfiltered.toSet()

        val lines = HashMap<Line, Int>()
        for (entry in blocksWithEdges.entries) {
            entry.value.forEach { newLine ->
                val currentCount = lines.getOrDefault(newLine, 0)
                lines[newLine] = currentCount + 1
            }
        }

        val filtered = lines.entries.filter { it.value == 1 }.map { it.key }
        SoundBounds.LOGGER.info("EDGE COUNT AFTER CULLING: ${filtered.size}")
        return filtered.map { Pair(it.vec1, it.vec2) }.toSet()
    }

    private fun getBlockPhysicalEdges(block: BlockPos): Set<Line> {
        val corners = getBlockCornerPositions(block)
        val edges = corners.flatMapIndexed { index, c ->
            corners.subList(index + 1, corners.size).mapNotNull {
                // corners are only adjacent if their positions are identical in 2 dimensions
                if (
                    (c.x == it.x && c.y == it.y) ||
                    (c.x == it.x && c.z == it.z) ||
                    (c.y == it.y && c.z == it.z)
                ) Line(c, it)
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

    private class Line(val vec1: Vec3i, val vec2: Vec3i) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Line

            return (vec1 == other.vec1 && vec2 == other.vec2) || (vec1 == other.vec2 && vec2 == other.vec1)
        }

        override fun hashCode(): Int {
            var result = vec1.hashCode()
            result = 31 * (result + vec2.hashCode())
            return result
        }
    }
}
