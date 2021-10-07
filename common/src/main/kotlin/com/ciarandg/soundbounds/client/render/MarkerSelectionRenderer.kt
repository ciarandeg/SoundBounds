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
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec2f
import kotlin.math.abs

object MarkerSelectionRenderer {
    private val selectionTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/selection.png")

    // PRECONDITION: matrixStack is aligned to World's [0, 0, 0]
    fun renderPlayerMarkerSelection(matrixStack: MatrixStack) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val marker1 = ClientPlayerModel.marker1
        val marker2 = ClientPlayerModel.marker2
        renderSelectionBorder(matrixStack, source, SBRenderLayer.getThickLines(), SBRenderLayer.getThinLines(), marker1, marker2)
        renderSelectionHighlight(matrixStack, source, SBRenderLayer.getSelectionHighlight(selectionTexture), RenderColor.WHITE, marker1, marker2)
    }

    private fun renderSelectionBorder(
        matrixStack: MatrixStack,
        source: VertexConsumerProvider.Immediate,
        markerLayer: RenderLayer,
        unionLayer: RenderLayer,
        marker1: BlockPos?,
        marker2: BlockPos?
    ) {
        val buffer = source.getBuffer(unionLayer)
        val m1Box = marker1?.toBox()?.expand(Z_INCREMENT)
        val m2Box = marker2?.toBox()?.expand(Z_INCREMENT)
        if (m1Box != null && m2Box != null) { drawBox(matrixStack, buffer, m1Box.union(m2Box), RenderColor.MAGENTA) }
        if (m1Box != m2Box) {
            if (m1Box != null) { drawBox(matrixStack, source.getBuffer(markerLayer), m1Box.expand(Z_INCREMENT), RenderColor.BLUE) }
            if (m2Box != null) { drawBox(matrixStack, source.getBuffer(markerLayer), m2Box.expand(Z_INCREMENT), RenderColor.RED) }
        }
        source.draw(unionLayer)
        source.draw(markerLayer)
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

        val markerBox =
            if (marker1 != null && marker2 != null) marker1.toBox().union(marker2.toBox())
            else marker1?.toBox() ?: marker2?.toBox() ?: return

        drawQuadBox(bufferBlockQuads, matrixPos, matrixNormal, markerBox.expand(Z_INCREMENT), color)

        source.draw(layer)
    }

    private fun drawQuadBox(
        bufferBlockQuads: VertexConsumer,
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        box: Box,
        color: RenderColor
    ) {
        val minX = box.minX.toFloat()
        val maxX = box.maxX.toFloat()
        val minY = box.minY.toFloat()
        val maxY = box.maxY.toFloat()
        val minZ = box.minZ.toFloat()
        val maxZ = box.maxZ.toFloat()

        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(maxX, minY, minZ), Vector3f(minX, maxY, minZ), Direction.NORTH, color)
        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(maxX, minY, maxZ), Vector3f(maxX, maxY, minZ), Direction.EAST, color)
        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, minY, maxZ), Vector3f(maxX, maxY, maxZ), Direction.SOUTH, color)
        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, minY, minZ), Vector3f(minX, maxY, maxZ), Direction.WEST, color)
        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, maxY, maxZ), Vector3f(maxX, maxY, minZ), Direction.UP, color)
        drawQuad(bufferBlockQuads, matrixPos, matrixNormal, Vector3f(minX, minY, minZ), Vector3f(maxX, minY, maxZ), Direction.DOWN, color)
    }

    private fun drawQuad(
        bufferBlockQuads: VertexConsumer,
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        bottomLeft: Vector3f,
        topRight: Vector3f,
        facing: Direction,
        color: RenderColor
    ) {
        val dimensions = Vector3f(
            abs(topRight.x - bottomLeft.x), abs(topRight.y - bottomLeft.y), abs(topRight.z - bottomLeft.z)
        )

        val dimensions2D = when (facing) {
            Direction.EAST -> Vec2f(dimensions.z, dimensions.y)
            Direction.NORTH -> Vec2f(dimensions.x, dimensions.y)
            Direction.SOUTH -> Vec2f(dimensions.x, dimensions.y)
            Direction.WEST -> Vec2f(dimensions.z, dimensions.y)
            Direction.UP -> Vec2f(dimensions.x, dimensions.z)
            Direction.DOWN -> Vec2f(dimensions.x, dimensions.z)
        }

        val bottomLeftUV = Vec2f(0.0f, dimensions2D.y)
        val bottomRightUV = Vec2f(dimensions2D.x, dimensions2D.y)
        val topLeftUV = Vec2f(0.0f, 0.0f)
        val topRightUV = Vec2f(dimensions2D.x, 0.0f)

        val corners = when (facing) {
            Direction.NORTH -> listOf(
                bottomLeft, Vector3f(topRight.x, bottomLeft.y, bottomLeft.z), // bottom right
                topRight, Vector3f(bottomLeft.x, topRight.y, bottomLeft.z) // top left
            )
            Direction.EAST -> listOf(
                bottomLeft, Vector3f(bottomLeft.x, bottomLeft.y, topRight.z),
                topRight, Vector3f(bottomLeft.x, topRight.y, bottomLeft.z)
            )
            Direction.SOUTH -> listOf(
                bottomLeft, Vector3f(topRight.x, bottomLeft.y, bottomLeft.z),
                topRight, Vector3f(bottomLeft.x, topRight.y, bottomLeft.z)
            )
            Direction.WEST -> listOf(
                bottomLeft, Vector3f(bottomLeft.x, bottomLeft.y, topRight.z),
                topRight, Vector3f(bottomLeft.x, topRight.y, bottomLeft.z)
            )
            Direction.UP -> listOf(
                bottomLeft, Vector3f(topRight.x, bottomLeft.y, bottomLeft.z),
                topRight, Vector3f(bottomLeft.x, bottomLeft.y, topRight.z)
            )
            Direction.DOWN -> listOf(
                bottomLeft, Vector3f(topRight.x, bottomLeft.y, bottomLeft.z),
                topRight, Vector3f(bottomLeft.x, bottomLeft.y, topRight.z)
            )
        }

        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, corners[0], color, bottomLeftUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, corners[1], color, bottomRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, corners[2], color, topRightUV)
        drawQuadVertex(bufferBlockQuads, matrixPos, matrixNormal, corners[3], color, topLeftUV)
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
