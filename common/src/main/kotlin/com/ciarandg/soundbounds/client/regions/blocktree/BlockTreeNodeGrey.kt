package com.ciarandg.soundbounds.client.regions.blocktree

internal class BlockTreeNodeGrey : BlockTreeNode {
    val children = listOf<BlockTreeNode>(
        BlockTreeNodeWhite(), // ---
        BlockTreeNodeWhite(), // +--
        BlockTreeNodeWhite(), // -+-
        BlockTreeNodeWhite(), // ++-
        BlockTreeNodeWhite(), // --+
        BlockTreeNodeWhite(), // +-+
        BlockTreeNodeWhite(), // -++
        BlockTreeNodeWhite()  // +++
    )

    override fun blockCount() = children.sumOf { it.blockCount() }

    override fun contains(element: Vec3iConst) = children.any { it.contains(element) }

    override fun canContain(block: Vec3iConst): Boolean {
        TODO("Not yet implemented")
    }

    override fun add(element: Vec3iConst): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(element: Vec3iConst): Boolean {
        TODO("Not yet implemented")
    }

    override fun iterator(): MutableIterator<Vec3iConst> {
        TODO("Not yet implemented")
    }
}