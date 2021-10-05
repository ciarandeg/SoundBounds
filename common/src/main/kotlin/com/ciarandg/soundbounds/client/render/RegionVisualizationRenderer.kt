package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.GraphRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import kotlin.math.max
import kotlin.math.min

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
        val blocks = allBlocksInRegion(region)
        val graph = GraphRegion(blocks)
        val wireframe = graph.getWireframe()
        for (edge in wireframe) {
            fun drawVertex(v: Vec3i) {
                val xyz = listOf(v.x, v.y, v.z).map { it.toFloat() }
                vertexConsumer.vertex(model, xyz[0], xyz[1], xyz[2]).color(color.red, color.green, color.blue, color.alpha).next()
            }
            drawVertex(edge.first)
            drawVertex(edge.second)
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
