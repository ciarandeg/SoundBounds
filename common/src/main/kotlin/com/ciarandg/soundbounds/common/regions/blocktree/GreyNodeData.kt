package com.ciarandg.soundbounds.common.regions.blocktree

import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

internal class GreyNodeData(
    val children: List<BlockTreeNode>
) {
    constructor(minPos: BlockPos, maxPos: BlockPos, childColor: BlockTreeNode.Color = BlockTreeNode.Color.WHITE) : this(
        if (BlockTreeNode.capacity(minPos, maxPos) > 8) partitionIntoNonAtomic(minPos, maxPos, childColor) else partitionIntoAtomic(minPos, maxPos, childColor)
    )

    fun findCorrespondingNode(block: BlockPos): BlockTreeNode = children.first { it.canContain(block) }

    fun copy() = GreyNodeData(children.map { it.copy() })

    companion object {
        // since we're dealing with discrete blocks, the area must be split with a bias toward one particular corner
        // otherwise, there would be overlaps or gaps between our children's areas
        private fun partitionIntoNonAtomic(minPos: BlockPos, maxPos: BlockPos, childColor: BlockTreeNode.Color): List<BlockTreeNode> {
            val middle = BlockPos(
                (maxPos.x + minPos.x) / 2,
                (maxPos.y + minPos.y) / 2,
                (maxPos.z + minPos.z) / 2
            )
            val westDownNorth = genNode(BlockPos(minPos.x, minPos.y, minPos.z), middle, childColor)
            val eastDownNorth = genNode(BlockPos(maxPos.x, minPos.y, minPos.z), middle.east(), childColor)
            val westUpNorth = genNode(BlockPos(minPos.x, maxPos.y, minPos.z), middle.up(), childColor)
            val eastUpNorth = genNode(BlockPos(maxPos.x, maxPos.y, minPos.z), middle.east().up(), childColor)
            val westDownSouth = genNode(BlockPos(minPos.x, minPos.y, maxPos.z), middle.south(), childColor)
            val eastDownSouth = genNode(BlockPos(maxPos.x, minPos.y, maxPos.z), middle.east().south(), childColor)
            val westUpSouth = genNode(BlockPos(minPos.x, maxPos.y, maxPos.z), middle.up().south(), childColor)
            val eastUpSouth =
                genNode(BlockPos(maxPos.x, maxPos.y, maxPos.z), middle.east().up().south(), childColor)
            return listOf(
                westDownNorth,
                eastDownNorth,
                westUpNorth,
                eastUpNorth,
                westDownSouth,
                eastDownSouth,
                westUpSouth,
                eastUpSouth
            )
        }

        private fun partitionIntoAtomic(minPos: BlockPos, maxPos: BlockPos, childColor: BlockTreeNode.Color): List<BlockTreeNode> {
            val everyBlock = ArrayList<BlockPos>()
            for (x in minPos.x..maxPos.x)
                for (y in minPos.y..maxPos.y)
                    for (z in minPos.z..maxPos.z) {
                        val block = BlockPos(x, y, z)
                        everyBlock.add(block)
                    }
            return everyBlock.map { genNode(it, it, childColor) }
        }

        private fun genNode(corner1: BlockPos, corner2: BlockPos, childColor: BlockTreeNode.Color): BlockTreeNode =
            with(justifiedCornerPair(corner1, corner2)) { BlockTreeNode(min, max, childColor) }

        private fun justifiedCornerPair(corner1: BlockPos, corner2: BlockPos) = object {
            val min = BlockPos(min(corner1.x, corner2.x), min(corner1.y, corner2.y), min(corner1.z, corner2.z))
            val max = BlockPos(max(corner1.x, corner2.x), max(corner1.y, corner2.y), max(corner1.z, corner2.z))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GreyNodeData

        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }
}
