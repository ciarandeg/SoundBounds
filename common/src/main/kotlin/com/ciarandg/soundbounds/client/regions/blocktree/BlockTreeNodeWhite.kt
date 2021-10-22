package com.ciarandg.soundbounds.client.regions.blocktree

import java.lang.IllegalStateException

internal class BlockTreeNodeWhite : BlockTreeNode {
    override fun blockCount() = 0

    override fun contains(element: Vec3iConst) = false

    override fun canContain(block: Vec3iConst) = false

    override fun add(element: Vec3iConst) = throw IllegalStateException("Can't add anything to a white node")

    override fun remove(element: Vec3iConst) = throw IllegalStateException("Can't remove anything from a white node")

    override fun iterator(): MutableIterator<Vec3iConst> = iterator()

    companion object {
        fun iterator() = object : MutableIterator<Vec3iConst> {
            override fun hasNext() = false
            override fun next() = throw IllegalStateException("Can't get next from empty iterator")
            override fun remove() = throw IllegalStateException("Can't remove from empty iterator")
        }
    }
}