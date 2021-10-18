package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.buttons.RedoButton
import com.ciarandg.soundbounds.client.ui.radial.buttons.UndoButton
import net.minecraft.client.options.KeyBinding
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW

class BatonMenu(private val origin: Vec2f) {
    private val commitModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val selectionModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    private val undoButton = UndoButton()
    private val redoButton = RedoButton()

    fun click(mousePos: Vec2f) {
        getHoveredButton(origin, mousePos).onClick()
    }

    fun draw() {}

    private fun getHoveredButton(origin: Vec2f, mousePos: Vec2f): RadialButton {
        TODO()
    }

    companion object {
        val binding = KeyBinding("Baton Radial Menu", GLFW.GLFW_KEY_EQUAL, SoundBounds.KEYBIND_CATEGORY)
    }
}
