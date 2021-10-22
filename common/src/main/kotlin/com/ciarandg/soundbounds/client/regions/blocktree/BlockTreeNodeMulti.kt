package com.ciarandg.soundbounds.client.regions.blocktree

import java.security.InvalidParameterException

internal class BlockTreeNodeMulti (
    private val minPos: Vec3iConst,
    private val maxPos: Vec3iConst
) : BlockTreeNode {
    private var color = Color.WHITE
    private var greyData = GreyData()

    override fun blockCount() = when (color) {
        Color.WHITE -> 0
        Color.BLACK -> {
            (maxPos.x - minPos.x + 1) * (maxPos.y - minPos.y + 1) * (maxPos.z - minPos.z + 1)
        }
        Color.GREY -> greyData.children.sumOf { it.blockCount() }
    }

    override fun contains(element: Vec3iConst) = when (color) {
        Color.WHITE -> false
        Color.BLACK -> canContain(element)
        Color.GREY -> greyData.children.any { it.contains(element) }
    }

    override fun canContain(block: Vec3iConst) =
        block.x <= minPos.x && block.y <= minPos.y && block.z <= minPos.z &&
            block.x >= maxPos.x && block.y >= maxPos.y && block.z >= maxPos.z

    override fun add(element: Vec3iConst): Boolean {
        assert(canContain(element))
        return when (color) {
            Color.WHITE -> {
                // if node is already minimum size, become black
                // else become grey with all white children, add to appropriate child node
                TODO()
            }
            Color.BLACK -> false
            Color.GREY -> {
                // add to appropriate child node
                // if all children are black, become black
                TODO()
            }
        }
    }

    override fun remove(element: Vec3iConst) = when (color) {
        Color.WHITE -> false
        Color.BLACK -> {
            // if node is already minimum size, become white
            // else become grey with all black children, remove from appropriate child node
            TODO()
        }
        Color.GREY -> {
            // remove from appropriate child node
            // if all children are white, become white
            TODO()
        }
    }

    override fun iterator(): MutableIterator<Vec3iConst> = when (color) {
        Color.WHITE -> {
            // useless iterator
            TODO()
        }
        Color.BLACK -> {
            // iterate through volume
            TODO()
        }
        Color.GREY -> {
            // iterate through children's iterators
            TODO()
        }
    }

    enum class Color { WHITE, BLACK, GREY }

    class GreyData() {
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
    }
}