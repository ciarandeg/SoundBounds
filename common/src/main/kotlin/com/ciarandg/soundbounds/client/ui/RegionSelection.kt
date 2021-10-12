package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.toBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3i

class RegionSelection(bounds: ClientRegionBounds = ClientRegionBounds(setOf())) {
    var bounds = bounds
        private set

    fun reset() { bounds = emptyBounds() }
    fun add(bounds: ClientRegionBounds) {
        this.bounds = ClientRegionBounds(
            this.bounds.blockSet.plus(bounds.blockSet),
        )
    }
    fun subtract(bounds: ClientRegionBounds) {
        this.bounds = ClientRegionBounds(
            this.bounds.blockSet.minus(bounds.blockSet),
        )
    }

    companion object {
        private fun emptyBounds() = ClientRegionBounds(setOf())

        fun fromBoxCorners(corner1: BlockPos?, corner2: BlockPos?): RegionSelection {
            fun getBounds(box: Box): ClientRegionBounds =
                ClientRegionBounds(blockifyBox(box))

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
