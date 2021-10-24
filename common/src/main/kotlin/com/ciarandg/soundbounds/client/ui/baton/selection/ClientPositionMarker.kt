package com.ciarandg.soundbounds.client.ui.baton.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos

class ClientPositionMarker(position: BlockPos) {
    val bounds = ClientRegionBounds(BlockTree.of(listOf(position)))
    fun getPos() = bounds.blockTree.first()
}
