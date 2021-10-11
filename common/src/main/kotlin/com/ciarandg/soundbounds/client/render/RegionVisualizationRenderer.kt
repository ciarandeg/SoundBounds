package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3i
import kotlin.math.abs

object RegionVisualizationRenderer {
    val uncommittedHighlightTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/uncommitted_selection.png")
    val committedHighlightTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/committed_selection.png")
    val existingRegionTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/existing_region.png")

    fun render(matrixStack: MatrixStack, region: ClientRegionBounds, texture: Identifier, color: RenderColor) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        renderFaceOutline(matrixStack, source.getBuffer(SBRenderLayer.getSelectionHighlight(texture)), region)
        renderRegionWireframe(matrixStack, source.getBuffer(SBRenderLayer.getThinLines()), region, color)
        renderFocalWireframes(matrixStack, source.getBuffer(SBRenderLayer.getThickLines()), region)
        source.draw()
    }

    private fun renderRegionWireframe(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, bounds: ClientRegionBounds, color: RenderColor) {
        renderWireframe(vertexConsumer, matrixStack.peek().model, bounds.getWireframe(), color)
    }

    private fun renderFocalWireframes(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, bounds: ClientRegionBounds) {
        val model = matrixStack.peek().model
        val colors = listOf(RenderColor.BLUE, RenderColor.RED, RenderColor.GREEN)
        val wireframe = bounds.getFocalWireframe()
        wireframe.forEachIndexed { focalIndex, focal ->
            renderWireframe(vertexConsumer, model, focal, colors[focalIndex % colors.size])
        }
    }

    private fun renderWireframe(vertexConsumer: VertexConsumer, model: Matrix4f, edges: Set<Pair<Vec3i, Vec3i>>, color: RenderColor) {
        edges.forEach { edge ->
            drawVertex(edge.first, color, vertexConsumer, model)
            drawVertex(edge.second, color, vertexConsumer, model)
        }
    }

    private fun drawVertex(v: Vec3i, color: RenderColor, buffer: VertexConsumer, model: Matrix4f) {
        val xyz = listOf(v.x, v.y, v.z).map { it.toFloat() }
        buffer.vertex(model, xyz[0], xyz[1], xyz[2]).color(color.red, color.green, color.blue, color.alpha).next()
    }

    private fun renderFaceOutline(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, bounds: ClientRegionBounds) {
        val outline = bounds.getFaceOutline()
        for (face in outline) {
            drawQuad(
                vertexConsumer, matrixStack.peek().model, matrixStack.peek().normal,
                with(face.first[0]) { Vector3f(x, y, z) },
                with(face.first[2]) { Vector3f(x, y, z) },
                face.second, RenderColor.WHITE
            )
        }
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
        val dimensions2D: Vec2f = with(
            Vector3f(
                abs(topRight.x - bottomLeft.x),
                abs(topRight.y - bottomLeft.y),
                abs(topRight.z - bottomLeft.z)
            )
        ) {
            when (facing) {
                Direction.EAST -> Vec2f(z, y)
                Direction.NORTH -> Vec2f(x, y)
                Direction.SOUTH -> Vec2f(x, y)
                Direction.WEST -> Vec2f(z, y)
                Direction.UP -> Vec2f(x, z)
                Direction.DOWN -> Vec2f(x, z)
            }
        }

        val bottomLeftUV = Vec2f(0.0f, dimensions2D.y)
        val bottomRightUV = Vec2f(dimensions2D.x, dimensions2D.y)
        val topLeftUV = Vec2f(0.0f, 0.0f)
        val topRightUV = Vec2f(dimensions2D.x, 0.0f)

        val corners = when (facing) {
            // bottom left, bottom right, top right, top left
            Direction.NORTH -> listOf(
                bottomLeft, Vector3f(topRight.x, bottomLeft.y, bottomLeft.z),
                topRight, Vector3f(bottomLeft.x, topRight.y, bottomLeft.z)
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
