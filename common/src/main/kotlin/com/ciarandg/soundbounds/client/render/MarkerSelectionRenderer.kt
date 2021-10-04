package com.ciarandg.soundbounds.client.render

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
    private const val INCREMENT = 0.001 // used to minimize z-fighting

    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun renderPlayerMarkerSelection(matrixStack: MatrixStack) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val marker1 = ClientPlayerModel.marker1?.let { blockPosToBox(it) }
        val marker2 = ClientPlayerModel.marker2?.let { blockPosToBox(it) }
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
            if (marker1 != null) { drawBox(matrixStack, source.getBuffer(layer), marker1.expand(INCREMENT), RenderColor.BLUE) }
            if (marker2 != null) { drawBox(matrixStack, source.getBuffer(layer), marker2.expand(INCREMENT), RenderColor.RED) }
        }
        source.draw(layer)
    }

    private fun drawBox(matrixStack: MatrixStack, renderBuffer: VertexConsumer, box: Box, color: RenderColor) {
        WorldRenderer.drawBox(matrixStack, renderBuffer, box, color.red, color.green, color.blue, color.alpha)
    }

    private fun blockPosToBox(pos: BlockPos) = Box(
        pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
        pos.x.toDouble() + 1.0, pos.y.toDouble() + 1.0, pos.z.toDouble() + 1.0
    ).expand(INCREMENT)
}
