package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.client.render.SBRenderLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f

abstract class RadialButton(
    val onClick: () -> Unit,
    private val startAngle: Double,
    private val endAngle: Double,
    private val defaultTexture: Identifier,
    private val hoverTexture: Identifier
) {
    fun isBisected(mousePos: PolarCoordinate): Boolean =
        with(mousePos) { return angle in startAngle..endAngle }

    open fun getCurrentTexture(mousePos: PolarCoordinate) =
        if (isBisected(mousePos)) hoverTexture else defaultTexture

    fun render(mousePos: PolarCoordinate, texWidth: Double, centerX: Double, centerY: Double) =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val buffer = getBuffer(SBRenderLayer.getBatonRadialMenu(getCurrentTexture(mousePos)))
            val minX = centerX - texWidth / 2
            val maxX = centerX + texWidth / 2
            val minY = centerY - texWidth / 2
            val maxY = centerY + texWidth / 2

            fun drawVertex(x: Double, y: Double, uv: Vec2f) =
                buffer.vertex(x, y, 0.0).texture(uv.x, uv.y).next()
            drawVertex(minX, maxY, Vec2f(0.0f, 1.0f))
            drawVertex(maxX, maxY, Vec2f(1.0f, 1.0f))
            drawVertex(maxX, minY, Vec2f(1.0f, 0.0f))
            drawVertex(minX, minY, Vec2f(0.0f, 0.0f))
            draw()
        }
}
