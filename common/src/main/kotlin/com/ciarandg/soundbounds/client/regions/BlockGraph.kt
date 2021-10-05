package com.ciarandg.soundbounds.client.regions

import net.minecraft.util.math.BlockPos

class BlockGraph(blocks: Set<BlockPos>) {
    private val blocks: MutableSet<BlockPos> = HashSet(blocks)
    fun addBlock(block: BlockPos) = blocks.add(block)
    fun addBlocks(blocks: Collection<BlockPos>) = blocks.forEach { this.blocks.add(it) }
    fun blocks(): Set<BlockPos> = blocks
    fun contains(block: BlockPos) = blocks.contains(block)
    fun adjacentBlocks(block: BlockPos) = setOf(
        BlockPos(block.x + 1, block.y, block.z),
        BlockPos(block.x - 1, block.y, block.z),
        BlockPos(block.x, block.y + 1, block.z),
        BlockPos(block.x, block.y - 1, block.z),
        BlockPos(block.x, block.y, block.z + 1),
        BlockPos(block.x, block.y, block.z - 1)
    ).filter { contains(it) }
}
