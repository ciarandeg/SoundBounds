package com.ciarandg.soundbounds.client.regions.blocktree

import net.minecraft.util.math.BlockPos

internal interface BlockTreeNode {
    fun blockCount(): Int
    fun contains(element: BlockPos): Boolean
    fun canContain(block: BlockPos): Boolean
    fun add(element: BlockPos): Boolean
    fun remove(element: BlockPos): Boolean
    fun iterator(): MutableIterator<BlockPos>
}
