package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.util.math.MatrixStack

object MarkerSelectionRenderer {
    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun render(matrixStack: MatrixStack) {
        RegionVisualizationRenderer.render(matrixStack, ClientPlayerModel.uncommittedSelection)
        RegionVisualizationRenderer.render(matrixStack, ClientPlayerModel.committedSelection)
    }
}
