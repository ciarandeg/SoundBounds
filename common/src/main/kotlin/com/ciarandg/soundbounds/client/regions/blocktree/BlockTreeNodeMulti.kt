package com.ciarandg.soundbounds.client.regions.blocktree

import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

internal class BlockTreeNodeMulti private constructor (
    private val minPos: Vec3iConst,
    private val maxPos: Vec3iConst,
    private var color: Color
) : BlockTreeNode {
    private var greyData = GreyData(minPos, maxPos, Color.WHITE) // should only be used when node is grey

    // constructor(minPos: Vec3iConst, maxPos: Vec3iConst) : this(minPos, maxPos, Color.WHITE)
    constructor(block: Vec3iConst) : this(block, block, Color.BLACK)
    constructor(node: BlockTreeNodeMulti, outsider: Vec3iConst) : this(
        Vec3iConst(min(node.minPos.x, outsider.x), min(node.minPos.y, outsider.y), min(node.minPos.z, outsider.z)),
        Vec3iConst(max(node.maxPos.x, outsider.x), max(node.maxPos.y, outsider.y), max(node.maxPos.z, outsider.z)),
        Color.GREY
    ) {
        val itr = node.iterator()
        while (itr.hasNext()) {
            add(itr.next())
        }
        add(outsider)
    }

    override fun blockCount(): Int = when (color) {
        Color.WHITE -> 0
        Color.BLACK -> {
            (maxPos.x - minPos.x + 1) * (maxPos.y - minPos.y + 1) * (maxPos.z - minPos.z + 1)
        }
        Color.GREY -> greyData.children.sumOf { it.blockCount() }
    }

    override fun contains(element: Vec3iConst): Boolean = when (color) {
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
                val result = greyData.findCorrespondingNode(element).add(element)
                if (greyData.children.all { it.color == Color.BLACK }) becomeBlack()
                result
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
            val result = greyData.findCorrespondingNode(element).remove(element)
            if (greyData.children.all { it.color == Color.WHITE }) becomeWhite()
            result
        }
    }

    override fun iterator(): MutableIterator<Vec3iConst> = when (color) {
        Color.WHITE -> whiteIterator
        Color.BLACK -> object : MutableIterator<Vec3iConst> {
            var current: Vec3iConst? = null
            val totalBlocks = blockCount()
            var index = 0

            val width = maxPos.x - minPos.x + 1
            val height = maxPos.y - minPos.y + 1

            override fun hasNext() = index < totalBlocks

            override fun next(): Vec3iConst {
                if (!hasNext()) throw IllegalStateException("Can't get next when hasNext is false")
                val next = indexToPos(index)
                index++
                current = next
                return current ?: throw ConcurrentModificationException()
            }

            override fun remove() {
                current?.let { remove(it) }
                    ?: throw IllegalStateException("Attempted to remove a value that doesn't exist")
                current = null
            }

            private fun indexToPos(i: Int): Vec3iConst {
                // Stolen from here because I'm lazy: https://stackoverflow.com/a/34363187
                val z = i / (width * height)
                val j = i - (z * width * height)
                val y = j / width
                val x = j % width
                return Vec3iConst(minPos.x + x, minPos.y + y, minPos.z + z)
            }
        }
        Color.GREY -> object : MutableIterator<Vec3iConst> {
            val children = greyData.children.map { it.iterator() }
            var current: Vec3iConst? = null

            override fun hasNext() = children.any { it.hasNext() }

            override fun next(): Vec3iConst {
                val result = children.first { it.hasNext() }.next()
                current = result
                return result
            }

            override fun remove() {
                current?.let { remove(it) }
                    ?: throw IllegalStateException("Attempted to remove a value that doesn't exist")
                current = null
            }
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
        private val midX = (maxPos.x - minPos.x) / 2
        private val midY = (maxPos.x - minPos.x) / 2
        private val midZ = (maxPos.x - minPos.x) / 2

        private val middle = Vec3iConst(midX, midY, midZ)

        private fun genNode(corner1: Vec3iConst, corner2: Vec3iConst, childColor: Color) : BlockTreeNodeMulti {
            val cornerMin = Vec3iConst(min(corner1.x, corner2.x), min(corner1.y, corner2.y), min(corner1.z, corner2.z))
            val cornerMax = Vec3iConst(max(corner1.x, corner2.x), min(corner1.y, corner2.y), min(corner1.z, corner2.z))
            return BlockTreeNodeMulti(cornerMin, cornerMax, childColor)
        }

        // since we're dealing with discrete blocks, the area must be split with a bias toward one particular corner
        // otherwise, there would be overlaps or gaps between our children's areas
        val children = listOf(
            genNode(Vec3iConst(minPos.x, minPos.y, minPos.z), middle, childColor), // ---
            genNode(Vec3iConst(maxPos.x, minPos.y, minPos.z), Vec3iConst(middle.x + 1, middle.y, middle.z), childColor), // +--
            genNode(Vec3iConst(minPos.x, maxPos.y, minPos.z), Vec3iConst(middle.x, middle.y + 1, middle.z), childColor), // -+-
            genNode(Vec3iConst(maxPos.x, maxPos.y, minPos.z), Vec3iConst(middle.x + 1, middle.y + 1, middle.z), childColor), // ++-
            genNode(Vec3iConst(minPos.x, minPos.y, maxPos.z), Vec3iConst(middle.x, middle.y, middle.z + 1), childColor), // --+
            genNode(Vec3iConst(maxPos.x, minPos.y, maxPos.z), Vec3iConst(middle.x + 1, middle.y, middle.z + 1), childColor), // +-+
            genNode(Vec3iConst(minPos.x, maxPos.y, maxPos.z), Vec3iConst(middle.x, middle.y + 1, middle.z + 1), childColor), // -++
            genNode(Vec3iConst(maxPos.x, maxPos.y, maxPos.z), Vec3iConst(middle.x + 1, middle.y + 1, middle.z + 1), childColor)  // +++
        )
        fun findCorrespondingNode(block: Vec3iConst) = children.first { it.canContain(block) }
    }

    companion object {
        private val whiteIterator = object : MutableIterator<Vec3iConst> {
            override fun hasNext() = false
            override fun next() = throw IllegalStateException("White node iterator never has a next value")
            override fun remove() = throw IllegalStateException("White node iterator has no values to remove")
        }
    }
}