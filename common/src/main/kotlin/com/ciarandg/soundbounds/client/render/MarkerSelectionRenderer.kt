package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer.committedHighlightTexture
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer.uncommittedHighlightTexture
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.util.math.MatrixStack

object MarkerSelectionRenderer {
    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun render(matrixStack: MatrixStack) {
        RegionVisualizationRenderer.render(matrixStack, ClientPlayerModel.uncommittedSelection.bounds, uncommittedHighlightTexture, RenderColor(64, 78, 160))
        RegionVisualizationRenderer.render(matrixStack, ClientPlayerModel.committedSelection.bounds, committedHighlightTexture, RenderColor(64, 160, 85))
    }
}
