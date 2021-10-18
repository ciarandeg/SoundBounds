package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.buttons.RedoButton
import com.ciarandg.soundbounds.client.ui.radial.buttons.UndoButton
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.options.KeyBinding
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
        super.render(matrices, mouseX, mouseY, delta)
        renderBackground(matrices)
    }

    fun click(mousePos: Vec2f) {
        getHoveredButton(origin, mousePos).onClick()
    }

    private fun getHoveredButton(origin: Vec2f, mousePos: Vec2f): RadialButton {
        TODO()
    }

    companion object {
        val binding = KeyBinding("Baton Radial Menu", GLFW.GLFW_KEY_EQUAL, SoundBounds.KEYBIND_CATEGORY)
    }
}
