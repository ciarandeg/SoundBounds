package com.ciarandg.soundbounds.client.ui.baton.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.toBox
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3i

// Wrapper class for sculpting a ClientRegionBounds
class RegionSelection(val bounds: ClientRegionBounds = ClientRegionBounds(BlockTree())) {
    fun reset() = bounds.blockTree.clear()
    fun add(bounds: ClientRegionBounds) =
        this.bounds.blockTree.addAll(bounds.blockTree)
    fun subtract(bounds: ClientRegionBounds) =
        this.bounds.blockTree.removeAll(bounds.blockTree)

    companion object {
        private fun emptyBounds() = ClientRegionBounds(BlockTree())

        fun fromBoxCorners(corner1: BlockPos?, corner2: BlockPos?): RegionSelection {
            fun getBounds(box: Box): ClientRegionBounds =
                ClientRegionBounds(BlockTree.of(blockifyBox(box)))

            return RegionSelection(
                when (corner1) {
                    null -> when (corner2) {
                        null -> emptyBounds()
                        else -> getBounds(corner2.toBox())
                    }
                    else -> when (corner2) {
                        null -> getBounds(corner1.toBox())
                        else -> getBounds(corner1.toBox().union(corner2.toBox()))
                    }
                }
            )
        }

        private fun blockifyBox(box: Box): Set<BlockPos> {
            val set: MutableSet<BlockPos> = HashSet()
            val minVec = Vec3i(box.minX, box.minY, box.minZ)
            val maxVec = Vec3i(box.maxX - 1, box.maxY - 1, box.maxZ - 1)
            for (x in minVec.x..maxVec.x) {
                for (y in minVec.y..maxVec.y) {
                    for (z in minVec.z..maxVec.z) {
                        set.add(BlockPos(x, y, z))
                    }
                }
            }
            return set
        }
    }
}
