package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class ClientPositionMarker(position: BlockPos) {
    val bounds = ClientRegionBounds(BlockTree.of(listOf(position)))
    fun getPos() = bounds.blockTree.first()

    companion object {
        fun fromPlayerRaytrace(player: PlayerEntity, tickDelta: Float): ClientPositionMarker {
            val hit = player.raycast(1000.0, tickDelta, true)
            val hitPos = hit.pos
            val hitBlock = with(hitPos) { BlockPos(x, y, z) }
            return ClientPositionMarker(hitBlock)
        }
    }
}
