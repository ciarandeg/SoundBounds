package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.render.toBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3i

class RegionSelection(var bounds: ClientRegionBounds = ClientRegionBounds(setOf())) {
    fun reset() { bounds = emptyBounds() }
    fun add(bounds: ClientRegionBounds) {
        this.bounds = ClientRegionBounds(
            this.bounds.blockSet.plus(bounds.blockSet),
            this.bounds.focals.plus(bounds.focals)
        )
    }
    fun subtract(bounds: ClientRegionBounds) {
        this.bounds = ClientRegionBounds(
            this.bounds.blockSet.minus(bounds.blockSet),
            this.bounds.focals.minus(bounds.focals)
        )
    }

    companion object {
        private fun emptyBounds() = ClientRegionBounds(setOf())

        fun fromBoxCorners(corner1: BlockPos?, corner2: BlockPos?, focalCorners: Boolean): RegionSelection {
            fun getBounds(box: Box, focals: Set<BlockPos>): ClientRegionBounds =
                ClientRegionBounds(blockifyBox(box), focals)

            return RegionSelection(
                when (corner1) {
                    null -> when (corner2) {
                        null -> emptyBounds()
                        else -> getBounds(corner2.toBox(), if (focalCorners) setOf(corner2) else setOf())
                    }
                    else -> when (corner2) {
                        null -> getBounds(corner1.toBox(), if (focalCorners) setOf(corner1) else setOf())
                        else -> getBounds(corner1.toBox().union(corner2.toBox()), if (focalCorners) setOf(corner1, corner2) else setOf())
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
