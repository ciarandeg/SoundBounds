package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.GraphRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3i

object RegionVisualizationRenderer {
    fun renderRegionVisualization(matrixStack: MatrixStack, region: ClientRegion) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val layer = SBRenderLayer.getThinLines()
        renderWireframe(matrixStack, source.getBuffer(layer), region)
        source.draw()
    }

    private fun renderWireframe(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, region: ClientRegion) {
        val model = matrixStack.peek().model
        val color = RenderColor.CYAN
        val wireframe = if (region === regionCache) {
            wireframeCache ?: throw IllegalStateException()
        } else {
            val wf = GraphRegion(region.blockSet.value).getWireframe()
            regionCache = region
            wireframeCache = wf
            wf
        }
        for (edge in wireframe) {
            fun drawVertex(v: Vec3i) {
                val xyz = listOf(v.x, v.y, v.z).map { it.toFloat() }
                vertexConsumer.vertex(model, xyz[0], xyz[1], xyz[2]).color(color.red, color.green, color.blue, color.alpha).next()
            }
            drawVertex(edge.first)
            drawVertex(edge.second)
        }
    }

    private var regionCache: ClientRegion? = null
    private var wireframeCache: Set<Pair<Vec3i, Vec3i>>? = null
}
