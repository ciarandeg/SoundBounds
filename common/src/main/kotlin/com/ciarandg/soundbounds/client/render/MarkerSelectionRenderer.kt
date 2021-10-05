package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
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
        val marker1 = ClientPlayerModel.marker1
        val marker2 = ClientPlayerModel.marker2
        renderSelectionBorder(matrixStack, source, RenderLayer.LINES, marker1, marker2)
        renderSelectionHighlight(matrixStack, source, RenderLayer.getEntitySolid(selectionTexture), RenderColor.WHITE, marker1, marker2)
    }

    private fun renderSelectionBorder(
        matrixStack: MatrixStack,
        source: VertexConsumerProvider.Immediate,
        layer: RenderLayer,
        marker1: BlockPos?,
        marker2: BlockPos?
    ) {
        val buffer = source.getBuffer(layer)
        val m1Box = marker1?.toBox()?.expand(Z_INCREMENT)
        val m2Box = marker2?.toBox()?.expand(Z_INCREMENT)
        if (m1Box != null && m2Box != null) { drawBox(matrixStack, buffer, m1Box.union(m2Box), RenderColor.MAGENTA) }
        if (m1Box != m2Box) {
            if (m1Box != null) { drawBox(matrixStack, source.getBuffer(layer), m1Box.expand(Z_INCREMENT), RenderColor.BLUE) }
            if (m2Box != null) { drawBox(matrixStack, source.getBuffer(layer), m2Box.expand(Z_INCREMENT), RenderColor.RED) }
        }
        source.draw(layer)
    }

    private fun drawBox(matrixStack: MatrixStack, renderBuffer: VertexConsumer, box: Box, color: RenderColor) {
        WorldRenderer.drawBox(matrixStack, renderBuffer, box, color.red, color.green, color.blue, color.alpha)
    }

    private fun renderSelectionHighlight(
        matrixStack: MatrixStack,
        source: VertexConsumerProvider.Immediate,
        layer: RenderLayer,
        color: RenderColor,
        marker1: BlockPos?,
        marker2: BlockPos?
    ) {
        val bufferBlockQuads = source.getBuffer(layer)
        val matrixPos = matrixStack.peek().model
        val matrixNormal = matrixStack.peek().normal

        val bottomLeftUV = Vec2f(0.0f, 1.0f)
        val bottomRightUV = Vec2f(1.0f, 1.0f)
        val topLeftUV = Vec2f(0.0f, 0.0f)
        val topRightUV = Vec2f(1.0f, 0.0f)

        val markerBox = marker1?.toBox() ?: marker2?.toBox() ?: return
        val minX = markerBox.minX.toFloat()
        val maxX = markerBox.maxX.toFloat()
        val minY = markerBox.minY.toFloat()
        val maxY = markerBox.maxY.toFloat()
        val minZ = markerBox.minZ.toFloat()
        val maxZ = markerBox.maxZ.toFloat()

        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, minY, minZ), color, bottomLeftUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(maxX, minY, minZ), color, bottomRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(maxX, maxY, minZ), color, topRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, maxY, minZ), color, topLeftUV)

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
            .color(color.red, color.green, color.blue, color.alpha)
            .texture(texUV.x, texUV.y)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(LightmapTextureManager.pack(15, 15))
            .normal(matrixNormal, 0.0f, 0.0f, 0.0f)
            .next()
    }
}
