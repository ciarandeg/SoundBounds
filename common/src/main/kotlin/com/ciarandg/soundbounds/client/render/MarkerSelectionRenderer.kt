package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

@Suppress("INACCESSIBLE_TYPE")
object MarkerSelectionRenderer {
    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun renderPlayerMarkerSelection(matrixStack: MatrixStack) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val marker1 = ClientPlayerModel.marker1?.toBox()
        val marker2 = ClientPlayerModel.marker2?.toBox()
        renderSelectionBorder(matrixStack, source, RenderLayer.LINES, marker1, marker2)
    }

    private fun renderSelectionBorder(
        matrixStack: MatrixStack,
        source: VertexConsumerProvider.Immediate,
        layer: RenderLayer,
        marker1: Box?,
        marker2: Box?
    ) {
        val buffer = source.getBuffer(layer)
        if (marker1 != null && marker2 != null) { drawBox(matrixStack, buffer, marker1.union(marker2), RenderColor.MAGENTA) }
        if (marker1 != marker2) {
            if (marker1 != null) { drawBox(matrixStack, source.getBuffer(layer), marker1.expand(Z_INCREMENT), RenderColor.BLUE) }
            if (marker2 != null) { drawBox(matrixStack, source.getBuffer(layer), marker2.expand(Z_INCREMENT), RenderColor.RED) }
        }
        source.draw(layer)
    }

    private fun drawBox(matrixStack: MatrixStack, renderBuffer: VertexConsumer, box: Box, color: RenderColor) {
        WorldRenderer.drawBox(matrixStack, renderBuffer, box, color.red, color.green, color.blue, color.alpha)
    }
}
