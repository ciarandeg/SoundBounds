package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer.committedHighlightTexture
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

object SelectionRenderer {
    private val cursorTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/cursor.png")

    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun render(matrixStack: MatrixStack, renderCursor: Boolean) {
        with(ClientPlayerModel) {
            renderSelection(matrixStack, committedSelection, committedHighlightTexture, RenderColor(64, 160, 85))
            renderSelection(matrixStack, uncommittedSelection, batonState.commitMode.texture, batonState.commitMode.wireframeColor)
            with(batonState) {
                if (renderCursor) cursor?.let { renderMarkerWithQuads(it, matrixStack, RenderColor.WHITE, cursorTexture) }
                marker1?.let { renderMarker(it, matrixStack, RenderColor.BLUE) }
                marker2?.let { renderMarker(it, matrixStack, RenderColor.RED) }
            }
        }
    }

    private fun renderSelection(matrixStack: MatrixStack, bounds: ClientRegionBounds, texture: Identifier, color: RenderColor) {
        RegionVisualizationRenderer.renderFilledWireframe(
            matrixStack, bounds, texture,
            SBRenderLayer.getThinLines(), color
        )
    }

    private fun renderMarkerWithQuads(marker: ClientPositionMarker, matrixStack: MatrixStack, wireframeColor: RenderColor, quadTexture: Identifier) {
        RegionVisualizationRenderer.renderFilledWireframe(
            matrixStack, marker.bounds, quadTexture,
            SBRenderLayer.getThickLines(), wireframeColor
        )
    }

    private fun renderMarker(marker: ClientPositionMarker, matrixStack: MatrixStack, color: RenderColor) {
        RegionVisualizationRenderer.renderWireframe(
            matrixStack, marker.bounds,
            SBRenderLayer.getThickLines(), color
        )
    }
}
