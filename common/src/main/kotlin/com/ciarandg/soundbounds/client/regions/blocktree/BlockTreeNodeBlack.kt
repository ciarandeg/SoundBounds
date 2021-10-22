package com.ciarandg.soundbounds.client.regions.blocktree

import java.security.InvalidParameterException

internal class BlockTreeNodeBlack(
    private val minPos: Vec3iConst,
    private val maxPos: Vec3iConst
) : BlockTreeNode {
    init {
        if (minPos.x > maxPos.x || minPos.y > maxPos.y || minPos.z > maxPos.z)
            throw InvalidParameterException("minPos must be <= maxPos in all dimensions")
    }

    override fun blockCount() = (maxPos.x - minPos.x + 1) * (maxPos.y - minPos.y + 1) * (maxPos.z - minPos.z + 1)

    override fun contains(element: Vec3iConst) =
        element.x <= minPos.x && element.y <= minPos.y && element.z <= minPos.z &&
            element.x >= maxPos.x && element.y >= maxPos.y && element.z >= maxPos.z

    override fun canContain(block: Vec3iConst) = contains(block)

    override fun add(element: Vec3iConst) = false

    override fun remove(element: Vec3iConst) = throw IllegalStateException("Can't remove from a black node")

    override fun iterator() = object : MutableIterator<Vec3iConst> {
        val xWidth = maxPos.x - minPos.x + 1
        val yWidth = maxPos.y - minPos.y + 1
        val zWidth = maxPos.z - minPos.z + 1

        val blockList: MutableList<Vec3iConst> = ArrayList()
        val listIterator = blockList.iterator()

        init {
            for (x in 0 until xWidth) {
                for (y in 0 until yWidth) {
                    for (z in 0 until zWidth) {
                        blockList.add(Vec3iConst(minPos.x + x, minPos.y + y, minPos.z + z))
                    }
                }
            }
        }

        override fun hasNext() = listIterator.hasNext()

        override fun next() = listIterator.next()

        override fun remove() = throw IllegalStateException("Can't remove from a black node iterator")
    }
}