package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec2f

@Suppress("INACCESSIBLE_TYPE")
object MarkerSelectionRenderer {
    val selectionTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/selection.png")

    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun renderPlayerMarkerSelection(matrixStack: MatrixStack) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val marker1 = ClientPlayerModel.marker1?.toBox()?.expand(Z_INCREMENT)
        val marker2 = ClientPlayerModel.marker2?.toBox()?.expand(Z_INCREMENT)
        renderSelectionBorder(matrixStack, source, RenderLayer.LINES, marker1, marker2)
        drawCubeQuads(matrixStack, source, RenderLayer.getEntitySolid(selectionTexture), RenderColor.WHITE)
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

    private fun drawCubeQuads(matrixStack: MatrixStack, source: VertexConsumerProvider.Immediate, layer: RenderLayer, color: RenderColor) {
        val bufferBlockQuads = source.getBuffer(layer)
        val matrixPos = matrixStack.peek().model
        val matrixNormal = matrixStack.peek().normal

        val bottomLeftUV = Vec2f(0.0f, 1.0f)
        val bottomRightUV = Vec2f(1.0f, 1.0f)
        val topLeftUV = Vec2f(0.0f, 0.0f)
        val topRightUV = Vec2f(1.0f, 0.0f)

        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(0.0f, 0.0f, 0.0f), color, bottomLeftUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(1.0f, 0.0f, 0.0f), color, bottomRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(1.0f, 1.0f, 0.0f), color, topRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(0.0f, 1.0f, 0.0f), color, topLeftUV)

        source.draw(layer)
    }

    private fun drawQuadVertex(
        bufferBlockQuads: VertexConsumer,
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        pos: Vector3f,
        color: RenderColor,
        texUV: Vec2f
    ) {
        bufferBlockQuads.vertex(matrixPos, pos.x, pos.y, pos.z)
            .color(color.red, color.blue, color.green, color.alpha)
            .texture(texUV.x, texUV.y)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(255)
            .normal(matrixNormal, 0.0f, 0.0f, 0.0f)
            .next()
    }
}
