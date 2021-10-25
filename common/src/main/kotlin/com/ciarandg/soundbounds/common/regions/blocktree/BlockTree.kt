package com.ciarandg.soundbounds.common.regions.blocktree

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos

class BlockTree private constructor(
    private var rootNode: BlockTreeNode?
) : MutableSet<BlockPos> {
    constructor() : this(null)

    override val size: Int
        get() = rootNode?.blockCount() ?: 0

    override fun add(element: BlockPos): Boolean =
        when (val root = rootNode) {
            null -> {
                rootNode = BlockTreeNode(element) // single black node
                true
            }
            else -> {
                if (root.canContain(element)) root.add(element)
                else {
                    rootNode = BlockTreeNode(root, element) // grey node encapsulating old root and element
                    true
                }
            }
        }

    override fun addAll(elements: Collection<BlockPos>): Boolean {
        var hasNewElement = false
        elements.forEach {
            val isNew = add(it)
            if (isNew) hasNewElement = true
        }
        return hasNewElement
    }

    override fun clear() {
        rootNode = null
    }

    override fun iterator(): MutableIterator<BlockPos> = when (val root = rootNode) {
        null -> object : MutableIterator<BlockPos> {
            override fun hasNext() = false
            override fun next() = throw IllegalStateException("Can't get next item for empty tree")
            override fun remove() = throw java.lang.IllegalStateException("Can't remove item from empty tree")
        }
        else -> root.iterator()
    }

    override fun remove(element: BlockPos): Boolean =
        when (val root = rootNode) {
            null -> false
            else -> if (root.canContain(element)) root.remove(element) else false
        }

    override fun removeAll(elements: Collection<BlockPos>): Boolean {
        var removedAnElement = false
        elements.forEach {
            val wasRemoved = remove(it)
            if (wasRemoved) removedAnElement = true
        }
        return removedAnElement
    }

    override fun retainAll(elements: Collection<BlockPos>): Boolean {
        val it = iterator()
        var elementWasRemoved = false
        while (it.hasNext()) {
            if (!elements.contains(it.next())) {
                elementWasRemoved = true
                it.remove()
            }
        }
        return elementWasRemoved
    }

    override fun contains(element: BlockPos) = rootNode?.contains(element) ?: false

    override fun containsAll(elements: Collection<BlockPos>) = elements.all { contains(it) }

    override fun isEmpty() = when (val root = rootNode) {
        null -> true
        else -> root.blockCount() == 0
    }

    fun serialize(): CompoundTag = rootNode?.serialize() ?: CompoundTag()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlockTree

        if (rootNode != other.rootNode) return false

        return true
    }

    override fun hashCode(): Int {
        return rootNode?.hashCode() ?: 0
    }

    companion object {
        fun of(elements: Collection<BlockPos>): BlockTree {
            val tree = BlockTree()
            tree.addAll(elements)
            return tree
        }

        fun deserialize(serialized: CompoundTag) =
            if (serialized.isEmpty) BlockTree()
            else BlockTree(BlockTreeNode.deserialize(serialized))
    }
}
