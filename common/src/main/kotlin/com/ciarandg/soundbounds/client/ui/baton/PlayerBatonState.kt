package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.client.options.KeyBinding
import org.lwjgl.glfw.GLFW

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    var cursor: ClientPositionMarker? = null
    var cursorMode = CursorMode.UNBOUNDED
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null

    companion object {
        const val UNBOUNDED_CURSOR_RANGE = 100.0
        const val DEFAULT_RADIUS_CURSOR_RANGE = 10.0
        const val MAX_RADIUS_CURSOR_RANGE = 20.0
        const val MIN_RADIUS_CURSOR_RANGE = 0.0
        val cursorModeBinding = KeyBinding("Baton Range Modifier", GLFW.GLFW_KEY_LEFT_CONTROL, SoundBounds.KEYBIND_CATEGORY)
    }
}
