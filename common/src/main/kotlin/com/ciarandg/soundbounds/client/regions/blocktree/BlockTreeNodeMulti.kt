package com.ciarandg.soundbounds.client.regions.blocktree

internal class BlockTreeNodeMulti private constructor (
    private val minPos: Vec3iConst,
    private val maxPos: Vec3iConst,
    private var color: Color
) : BlockTreeNode {
    private var greyData = GreyData(minPos, maxPos, Color.WHITE) // should only be used when node is grey

    constructor(minPos: Vec3iConst, maxPos: Vec3iConst) : this(minPos, maxPos, Color.WHITE)

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
                if (isAtomic()) {
                    becomeBlack()
                    true
                }
                else {
                    becomeGreyWhiteChildren()
                    add(element)
                }
            }
            Color.BLACK -> false
            Color.GREY -> {
                // add to appropriate child node
                // if all children are black, become black
                TODO()
            }
        }
    }

    override fun remove(element: Vec3iConst): Boolean = when (color) {
        Color.WHITE -> false
        Color.BLACK -> {
            if (isAtomic()) {
                becomeWhite()
                true
            } else {
                becomeGreyBlackChildren()
                remove(element)
            }
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

    private fun isAtomic() = minPos == maxPos

    private fun becomeWhite() {
        color = Color.WHITE
    }
    private fun becomeBlack() {
        color = Color.BLACK
    }
    private fun becomeGreyWhiteChildren() {
        color = Color.GREY
        greyData = GreyData(minPos, maxPos, Color.WHITE)
    }
    private fun becomeGreyBlackChildren() {
        color = Color.GREY
        greyData = GreyData(minPos, maxPos, Color.BLACK)
    }

    enum class Color { WHITE, BLACK, GREY }

    class GreyData(minPos: Vec3iConst, maxPos: Vec3iConst, childColor: Color = Color.WHITE) {
        val children = listOf<BlockTreeNode>(
            BlockTreeNodeMulti(minPos, maxPos, childColor), // ---
            BlockTreeNodeMulti(minPos, maxPos, childColor), // +--
            BlockTreeNodeMulti(minPos, maxPos, childColor), // -+-
            BlockTreeNodeMulti(minPos, maxPos, childColor), // ++-
            BlockTreeNodeMulti(minPos, maxPos, childColor), // --+
            BlockTreeNodeMulti(minPos, maxPos, childColor), // +-+
            BlockTreeNodeMulti(minPos, maxPos, childColor), // -++
            BlockTreeNodeMulti(minPos, maxPos, childColor)  // +++
        )
    }
}