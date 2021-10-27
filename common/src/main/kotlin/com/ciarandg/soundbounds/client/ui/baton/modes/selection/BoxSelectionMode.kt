package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer
import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos

class BoxSelectionMode {
    private var marker1: ClientPositionMarker? = null
    private var marker2: ClientPositionMarker? = null

    fun setFirstMarker(pos: BlockPos) {
        marker1 = ClientPositionMarker(pos)
    }

    fun setSecondMarker(pos: BlockPos) {
        marker2 = ClientPositionMarker(pos)
    }

    fun getSelection() = ClientRegionBounds(
        marker1?.let { m1 ->
            marker2?.let { m2 ->
                BlockTree.fromBoxCorners(m1.getPos(), m2.getPos())
            } ?: BlockTree()
        } ?: BlockTree()
    )

    fun renderMarkers(matrixStack: MatrixStack) {
        marker1?.let { renderMarker(it, matrixStack, RenderColor.BLUE) }
        marker2?.let { renderMarker(it, matrixStack, RenderColor.RED) }
    }

    private fun renderMarker(marker: ClientPositionMarker, matrixStack: MatrixStack, color: RenderColor) {
        RegionVisualizationRenderer.renderWireframe(
            matrixStack, marker.bounds,
            SBRenderLayer.getThickLines(), color
        )
    }
}
