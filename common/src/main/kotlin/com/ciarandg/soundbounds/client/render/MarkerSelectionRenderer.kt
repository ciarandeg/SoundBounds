package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer.committedHighlightTexture
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer.uncommittedHighlightTexture
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.util.math.MatrixStack

object MarkerSelectionRenderer {
    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun render(matrixStack: MatrixStack) {
        RegionVisualizationRenderer.renderFilledWireframe(
            matrixStack, ClientPlayerModel.uncommittedSelection.bounds, uncommittedHighlightTexture,
            SBRenderLayer.getThinLines(), RenderColor(64, 78, 160)
        )
        RegionVisualizationRenderer.renderWireframe(
            matrixStack, ClientRegionBounds(setOf(ClientPlayerModel.marker1).filterNotNull().toSet()),
            SBRenderLayer.getThickLines(), RenderColor.BLUE
        )
        RegionVisualizationRenderer.renderWireframe(
            matrixStack, ClientRegionBounds(setOf(ClientPlayerModel.marker2).filterNotNull().toSet()),
            SBRenderLayer.getThickLines(), RenderColor.RED
        )
        RegionVisualizationRenderer.renderFilledWireframe(
            matrixStack, ClientPlayerModel.committedSelection.bounds, committedHighlightTexture,
            SBRenderLayer.getThinLines(), RenderColor(64, 160, 85)
        )
    }
}
