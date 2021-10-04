package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.GraphRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

object RegionVisualizationRenderer {
    fun renderRegionVisualization(matrixStack: MatrixStack, region: ClientRegion) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val layer = RenderLayer.LINES
        val buffer = source.getBuffer(layer)

        renderWireframe(matrixStack, source.getBuffer(layer), region)

        source.draw()
    }

    private fun renderWireframe(matrixStack: MatrixStack, vertexConsumer: VertexConsumer, region: ClientRegion) {
        val model = matrixStack.peek().model
        val color = RenderColor.CYAN
        val blocks = allBlocksInRegion(region)
        val graph = GraphRegion(blocks)
        val wireframe = graph.getWireframe()
        for (edge in wireframe) {
            val x1 = edge.first.x.toFloat()
            val y1 = edge.first.y.toFloat()
            val z1 = edge.first.z.toFloat()
            val x2 = edge.second.x.toFloat()
            val y2 = edge.second.y.toFloat()
            val z2 = edge.second.z.toFloat()
            vertexConsumer.vertex(model, x1, y1, z1).color(color.red, color.green, color.blue, color.alpha).next()
            vertexConsumer.vertex(model, x2, y2, z2).color(color.red, color.green, color.blue, color.alpha).next()
        }
    }

    private fun allBlocksInRegion(region: ClientRegion) = region.data.volumes.flatMap {
        val first = it.first
        val second = it.second

        val minX = min(first.x, second.x)
        val maxX = max(first.x, second.x)
        val minY = min(first.y, second.y)
        val maxY = max(first.y, second.y)
        val minZ = min(first.z, second.z)
        val maxZ = max(first.z, second.z)

        val blocks: MutableList<BlockPos> = ArrayList()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    blocks.add(BlockPos(x, y, z))
                }
            }
        }
        blocks
    }.toSet()
}
