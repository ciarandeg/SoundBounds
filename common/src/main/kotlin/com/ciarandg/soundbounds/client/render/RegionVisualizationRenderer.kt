package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.regions.ClientRegion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

object RegionVisualizationRenderer {
    fun renderRegionVisualization(matrixStack: MatrixStack, region: ClientRegion) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val layer = RenderLayer.LINES
        val buffer = source.getBuffer(layer)

        renderWireframe(region)

        source.draw()
    }

    private fun renderWireframe(region: ClientRegion) {
        val blocks = allBlocksInRegion(region)
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
    }
}