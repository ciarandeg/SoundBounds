package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.PolarCoordinate
import com.ciarandg.soundbounds.client.ui.radial.RadialFolder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import java.util.Stack
import kotlin.math.min

class BatonMenuScreen : Screen(LiteralText("Bounds Baton Menu")) {
    private val buttonGroups: Stack<MenuButtonGroup> = Stack()

    init { buttonGroups.push(BatonMenuPrimaryGroup()) }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        val textureWidth = min(width, height) * 0.75
        renderMenu(getMousePosPolar(mouseX, mouseY), textureWidth, width.toDouble() / 2, height.toDouble() / 2)
        if (SHOW_DEBUG_LINE) renderDebugLine(mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        when (button) {
            GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                when (val hovered = buttonGroups.peek().getHoveredButton(getMousePosPolar(mouseX, mouseY))) {
                    is RadialFolder -> buttonGroups.push(hovered.getSubGroup())
                    else -> {
                        hovered.onClick()
                        onClose()
                    }
                }
            }
            GLFW.GLFW_MOUSE_BUTTON_RIGHT -> if (buttonGroups.size > 1) buttonGroups.pop() else onClose()
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun renderDebugLine(mouseX: Int, mouseY: Int) =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val buffer = getBuffer(RenderLayer.getLines())
            with(renderLineColor) {
                buffer.vertex(width.toDouble() / 2, height.toDouble() / 2, 0.0).color(red, green, blue, alpha).next()
                buffer.vertex(mouseX.toDouble(), mouseY.toDouble(), 0.0).color(red, green, blue, alpha).next()
            }
            draw()
        }

    private fun renderMenu(mousePos: PolarCoordinate, texWidth: Double, centerX: Double, centerY: Double) =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val hoveredButton = buttonGroups.peek().getHoveredButton(mousePos)
            val buffer = getBuffer(SBRenderLayer.getBatonRadialMenu(hoveredButton.hoverTexture))
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

    private fun getMousePosPolar(mouseX: Int, mouseY: Int) = getMousePosPolar(mouseX.toDouble(), mouseY.toDouble())
    private fun getMousePosPolar(mouseX: Double, mouseY: Double): PolarCoordinate {
        val centerX = width / 2.0
        val centerY = height / 2.0
        return PolarCoordinate.fromCartesian(mouseX - centerX, centerY - mouseY)
    }

    companion object {
        const val SHOW_DEBUG_LINE = true
        val renderLineColor = RenderColor.GREEN
    }
}
