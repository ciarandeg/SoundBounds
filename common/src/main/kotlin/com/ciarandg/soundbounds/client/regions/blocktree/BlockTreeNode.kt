package com.ciarandg.soundbounds.client.regions.blocktree

internal interface BlockTreeNode {
    fun blockCount(): Int
    fun contains(element: Vec3iConst): Boolean
    fun canContain(block: Vec3iConst): Boolean
    fun add(element: Vec3iConst): Boolean
    fun remove(element: Vec3iConst): Boolean
    fun iterator(): MutableIterator<Vec3iConst>
}
