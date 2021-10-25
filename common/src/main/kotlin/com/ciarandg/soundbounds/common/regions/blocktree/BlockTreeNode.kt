package com.ciarandg.soundbounds.common.regions.blocktree

import com.ciarandg.soundbounds.common.regions.RegionData
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

internal class BlockTreeNode(
    val minPos: BlockPos,
    val maxPos: BlockPos,
    private var color: Color,
    private var greyData: GreyNodeData? = when (color) {
        Color.WHITE -> null
        Color.BLACK -> null
        Color.GREY -> GreyNodeData(minPos, maxPos)
    },
) {
    private val isAtomic = minPos == maxPos

    constructor(block: BlockPos) : this(block, block, Color.BLACK)
    constructor(node: BlockTreeNode, outsider: BlockPos) : this(
        BlockPos(min(node.minPos.x, outsider.x), min(node.minPos.y, outsider.y), min(node.minPos.z, outsider.z)),
        BlockPos(max(node.maxPos.x, outsider.x), max(node.maxPos.y, outsider.y), max(node.maxPos.z, outsider.z)),
        Color.GREY
    ) {
        val itr = node.iterator()
        while (itr.hasNext()) {
            add(itr.next())
        }
        add(outsider)
    }

    fun blockCount(): Int = when (color) {
        Color.WHITE -> 0
        Color.BLACK -> capacity(minPos, maxPos).toInt()
        Color.GREY -> greyData?.children?.sumOf { it.blockCount() } ?: throw GreyMustHaveDataException()
    }

    fun contains(element: BlockPos): Boolean = when (color) {
        Color.WHITE -> false
        Color.BLACK -> canContain(element)
        Color.GREY -> greyData?.children?.any { it.contains(element) } ?: throw GreyMustHaveDataException()
    }

    fun canContain(block: BlockPos) =
        minPos.x <= block.x && minPos.y <= block.y && minPos.z <= block.z &&
            maxPos.x >= block.x && maxPos.y >= block.y && maxPos.z >= block.z

    fun add(element: BlockPos): Boolean = when (color) {
        Color.WHITE -> if (isAtomic) { becomeBlack(); true } else { becomeGreyWhiteChildren(); add(element) }
        Color.BLACK -> false
        Color.GREY -> {
            val data = greyData ?: throw GreyMustHaveDataException()
            val result = data.findCorrespondingNode(element).add(element)
            if (data.children.all { it.color == Color.BLACK }) becomeBlack()
            result
        }
    }

    fun remove(element: BlockPos): Boolean = when (color) {
        Color.WHITE -> false
        Color.BLACK -> if (isAtomic) { becomeWhite(); true } else { becomeGreyBlackChildren(); remove(element) }
        Color.GREY -> {
            val data = greyData ?: throw GreyMustHaveDataException()
            val result = data.findCorrespondingNode(element).remove(element)
            if (data.children.all { it.color == Color.WHITE }) becomeWhite()
            result
        }
    }

    fun iterator(): MutableIterator<BlockPos> = when (color) {
        Color.WHITE -> whiteIterator
        Color.BLACK -> object : MutableIterator<BlockPos> {
            var current: BlockPos? = null
            val totalBlocks = blockCount()
            var index = 0

            val width = maxPos.x - minPos.x + 1
            val height = maxPos.y - minPos.y + 1

            override fun hasNext() = index < totalBlocks

            override fun next(): BlockPos {
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

            private fun indexToPos(i: Int): BlockPos {
                // Stolen from here because I'm lazy: https://stackoverflow.com/a/34363187
                val z = i / (width * height)
                val j = i - (z * width * height)
                val y = j / width
                val x = j % width
                return BlockPos(minPos.x + x, minPos.y + y, minPos.z + z)
            }
        }
        Color.GREY -> object : MutableIterator<BlockPos> {
            val children = greyData?.children?.map { it.iterator() }
            var current: BlockPos? = null

            override fun hasNext() = children?.any { it.hasNext() } ?: throw GreyMustHaveDataException()

            override fun next(): BlockPos {
                val result = children?.first { it.hasNext() }?.next() ?: throw GreyMustHaveDataException()
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

    private fun becomeWhite() {
        color = Color.WHITE
        greyData = null
    }
    private fun becomeBlack() {
        color = Color.BLACK
        greyData = null
    }
    private fun becomeGreyWhiteChildren() {
        color = Color.GREY
        greyData = GreyNodeData(minPos, maxPos, Color.WHITE)
    }
    private fun becomeGreyBlackChildren() {
        color = Color.GREY
        greyData = GreyNodeData(minPos, maxPos, Color.BLACK)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockTreeNode

        if (minPos != other.minPos) return false
        if (maxPos != other.maxPos) return false
        if (color != other.color) return false
        if (greyData != other.greyData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minPos.hashCode()
        result = 31 * result + maxPos.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + greyData.hashCode()
        return result
    }

    internal enum class Color { WHITE, BLACK, GREY }

    internal fun serialize(): CompoundTag {
        val tag = CompoundTag()
        tag.put("minPos", RegionData.blockPosToTag(minPos))
        tag.put("maxPos", RegionData.blockPosToTag(maxPos))
        tag.putString("color", color.name)
        if (color == Color.GREY) {
            val data = greyData ?: throw GreyMustHaveDataException()
            val childrenTag = ListTag()
            childrenTag.addAll(data.children.map { it.serialize() })
            tag.put("children", childrenTag)
        }
        return tag
    }

    companion object {
        private class GreyMustHaveDataException : IllegalStateException()

        private val whiteIterator = object : MutableIterator<BlockPos> {
            override fun hasNext() = false
            override fun next() = throw IllegalStateException("White node iterator never has a next value")
            override fun remove() = throw IllegalStateException("White node iterator has no values to remove")
        }

        fun capacity(minPos: BlockPos, maxPos: BlockPos): Long {
            val width = maxPos.x - minPos.x + 1
            val height = maxPos.y - minPos.y + 1
            val depth = maxPos.z - minPos.z + 1
            return width.toLong() * height.toLong() * depth.toLong()
        }

        fun deserialize(tag: CompoundTag): BlockTreeNode {
            val minPos = RegionData.tagToBlockPos(tag.getCompound("minPos"))
            val maxPos = RegionData.tagToBlockPos(tag.getCompound("maxPos"))
            val color = Color.valueOf(tag.getString("color"))

            var greyData: GreyNodeData? = null
            if (color == Color.GREY) {
                val childrenListTag = tag.getList("children", 10)
                val children = childrenListTag.map { deserialize(it as CompoundTag) }
                greyData = GreyNodeData(children)
            }

            return BlockTreeNode(minPos, maxPos, color, greyData)
        }
    }
}
