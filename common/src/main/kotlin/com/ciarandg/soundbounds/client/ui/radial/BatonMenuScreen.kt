package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.radial.buttons.RedoButton
import com.ciarandg.soundbounds.client.ui.radial.buttons.UndoButton
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

class BatonMenuScreen : Screen(LiteralText("Bounds Baton Menu")) {
    private val origin = client?.mouse?.let { Vec2f(it.x.toFloat(), it.y.toFloat()) } ?: Vec2f(0.0f, 0.0f)
    private val commitModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val selectionModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val undoButton = UndoButton()
    private val redoButton = RedoButton()

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        renderMenu()
        if (SHOW_DEBUG_LINE) renderDebugLine(mouseX, mouseY)
        val polar = PolarCoord.fromScreenCoords(mouseX, mouseY, width, height)
        SoundBounds.LOGGER.info("RADIUS: ${polar.radius} ANGLE: ${polar.angle}")
    }

    private fun renderDebugLine(mouseX: Int, mouseY: Int) =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val buffer = getBuffer(RenderLayer.getLines())
            buffer.vertex(width.toDouble() / 2, height.toDouble() / 2, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            buffer.vertex(mouseX.toDouble(), mouseY.toDouble(), 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            draw()
        }

    private fun renderMenu() =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val buffer = getBuffer(SBRenderLayer.getBatonRadialMenu(menuTexture))
            val centerX = width.toDouble() / 2
            val centerY = height.toDouble() / 2
            val menuLength = min(width, height) * 0.75
            val minX = centerX - menuLength / 2
            val maxX = centerX + menuLength / 2
            val minY = centerY - menuLength / 2
            val maxY = centerY + menuLength / 2

            fun drawVertex(x: Double, y: Double, uv: Vec2f) =
                buffer.vertex(x, y, 0.0).texture(uv.x, uv.y).next()
            drawVertex(minX, maxY, Vec2f(0.0f, 1.0f))
            drawVertex(maxX, maxY, Vec2f(1.0f, 1.0f))
            drawVertex(maxX, minY, Vec2f(1.0f, 0.0f))
            drawVertex(minX, minY, Vec2f(0.0f, 0.0f))
            draw()
        }

    private data class PolarCoord(val radius: Double, val angle: Double) {
        companion object {
            fun fromScreenCoords(mouseX: Int, mouseY: Int, width: Int, height: Int) =
                fromScreenCoords(mouseX.toDouble(), mouseY.toDouble(), width.toDouble() / 2, height.toDouble() / 2)
            fun fromScreenCoords(mouseX: Double, mouseY: Double, centerX: Double, centerY: Double) =
                fromCartesian(mouseX - centerX, centerY - mouseY)
            fun fromCartesian(x: Double, y: Double) = PolarCoord(radiusFromCartesian(x, y), angleFromCartesian(x, y))

            private fun radiusFromCartesian(x: Double, y: Double) = hypot(x, y).let {
                when {
                    it.isNaN() -> maxOf(x, y)
                    else -> it
                }
            }

            private fun angleFromCartesian(x: Double, y: Double) = atan2(x, y)
        }
    }

    companion object {
        const val SHOW_DEBUG_LINE = true
        val binding = KeyBinding("Baton Radial Menu", GLFW.GLFW_KEY_EQUAL, SoundBounds.KEYBIND_CATEGORY)
        private val menuTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/menu.png")
    }
}
