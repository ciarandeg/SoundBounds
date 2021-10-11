package com.ciarandg.soundbounds.client.regions

import net.minecraft.util.math.BlockPos

class ClientPositionMarker(position: BlockPos) {
    val bounds = ClientRegionBounds(setOf(position))
    fun getPos() = bounds.blockSet.first()
}
