package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.buttons.RedoButton
import com.ciarandg.soundbounds.client.ui.radial.buttons.UndoButton
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW

class BatonMenuScreen : Screen(LiteralText("Bounds Baton Menu")) {
    private val origin = client?.mouse?.let { Vec2f(it.x.toFloat(), it.y.toFloat()) } ?: Vec2f(0.0f, 0.0f)
    private val commitModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val selectionModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val undoButton = UndoButton()
    private val redoButton = RedoButton()

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        if (SHOW_DEBUG_LINE) renderDebugLine(mouseX, mouseY)
    }

    private fun renderDebugLine(mouseX: Int, mouseY: Int) =
        with(MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers) {
            val buffer = getBuffer(RenderLayer.getLines())
            buffer.vertex(width.toDouble() / 2, height.toDouble() / 2, 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            buffer.vertex(mouseX.toDouble(), mouseY.toDouble(), 0.0).color(1.0f, 1.0f, 1.0f, 1.0f).next()
            draw()
        }

    fun click(mousePos: Vec2f) {
        getHoveredButton(origin, mousePos).onClick()
    }

    private fun getHoveredButton(origin: Vec2f, mousePos: Vec2f): RadialButton {
        TODO()
    }

    companion object {
        const val SHOW_DEBUG_LINE = true
        val binding = KeyBinding("Baton Radial Menu", GLFW.GLFW_KEY_EQUAL, SoundBounds.KEYBIND_CATEGORY)
    }
}
