package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer
import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos

abstract class AbstractSelectionMode {
    protected var marker1: ClientPositionMarker? = null
    protected var marker2: ClientPositionMarker? = null

    fun setFirstMarker(pos: BlockPos) {
        marker1 = ClientPositionMarker(pos)
    }

    fun setSecondMarker(pos: BlockPos) {
        marker2 = ClientPositionMarker(pos)
    }

    abstract fun getSelection(): ClientRegionBounds

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
