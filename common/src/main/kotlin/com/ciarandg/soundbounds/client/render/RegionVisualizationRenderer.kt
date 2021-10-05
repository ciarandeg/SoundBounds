package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.GraphRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3i

object RegionVisualizationRenderer {
    fun renderRegionVisualization(matrixStack: MatrixStack, region: ClientRegion) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val layer = RenderLayer.LINES
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
}
