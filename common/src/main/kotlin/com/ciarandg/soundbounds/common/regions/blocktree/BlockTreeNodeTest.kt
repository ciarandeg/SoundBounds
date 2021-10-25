package com.ciarandg.soundbounds.common.regions.blocktree

import net.minecraft.util.math.BlockPos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BlockTreeNodeTest {
    @Test
    fun blockCount() {
        val node = BlockTreeNode(BlockPos(0, 0, 0))
        assertEquals(node.blockCount(), 1)
        node.remove(BlockPos(0, 0, 0))
        assertEquals(node.blockCount(), 0)
    }

    @Test
    fun contains() {
    }

    @Test
    fun canContain() {
    }

    @Test
    fun add() {
    }

    @Test
    fun remove() {
    }

    @Test
    operator fun iterator() {
    }

    @Test
    fun serialize() {
    }

    @Test
    fun getMinPos() {
    }

    @Test
    fun getMaxPos() {
    }
}
