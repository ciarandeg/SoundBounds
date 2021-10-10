package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object MarkerSelectionRenderer {
    val selectionTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/selection.png")

    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun renderPlayerMarkerSelection(matrixStack: MatrixStack) {
        val marker1 = ClientPlayerModel.marker1
        val marker2 = ClientPlayerModel.marker2
        renderSelection(matrixStack, marker1, marker2)
    }

    private fun renderSelection(
        matrixStack: MatrixStack,
        marker1: BlockPos?,
        marker2: BlockPos?
    ) {
        val bounds = ClientRegionBounds.fromBoxCorners(marker1, marker2, true)
        if (bounds != null) RegionVisualizationRenderer.renderRegionVisualization(matrixStack, bounds)
    }
}
