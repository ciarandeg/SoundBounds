package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer
import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos

class BoxSelectionMode : AbstractSelectionMode() {
    override fun getSelection() = ClientRegionBounds(
        marker1?.let { m1 ->
            marker2?.let { m2 ->
                BlockTree.fromBoxCorners(m1.getPos(), m2.getPos())
            }
        } ?: BlockTree()
    )
}
