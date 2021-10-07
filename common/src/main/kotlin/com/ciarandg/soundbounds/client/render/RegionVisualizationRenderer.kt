package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.GraphRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.Vec3i

object RegionVisualizationRenderer {
    fun renderRegionVisualization(matrixStack: MatrixStack, region: ClientRegion) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val layer = SBRenderLayer.getThinLines()
        renderFaceOutline(matrixStack, source.getBuffer(SBRenderLayer.getSelectionHighlight(MarkerSelectionRenderer.selectionTexture)), region)
        renderWireframe(matrixStack, source.getBuffer(layer), region)
        source.draw()
    }

    private fun renderWireframe(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, region: ClientRegion) {
        val model = matrixStack.peek().model
        val color = RenderColor.CYAN
        val wireframe = GraphRegion(region.blockSet).getWireframe()
        for (edge in wireframe) {
            fun drawVertex(v: Vec3i) {
                val xyz = listOf(v.x, v.y, v.z).map { it.toFloat() }
                vertexConsumer.vertex(model, xyz[0], xyz[1], xyz[2]).color(color.red, color.green, color.blue, color.alpha).next()
            }
            drawVertex(edge.first)
            drawVertex(edge.second)
        }
    }

    private fun renderFaceOutline(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, region: ClientRegion) {
        val outline = GraphRegion(region.blockSet).getFaceOutline()
        for (face in outline) {
            MarkerSelectionRenderer.drawQuad(
                vertexConsumer, matrixStack.peek().model, matrixStack.peek().normal,
                with(face.first[0]) { Vector3f(x.toFloat(), y.toFloat(), z.toFloat()) },
                with(face.first[2]) { Vector3f(x.toFloat(), y.toFloat(), z.toFloat()) },
                face.second, RenderColor.WHITE
            )
        }
    }
}
