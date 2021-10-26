package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.client.options.KeyBinding
import org.lwjgl.glfw.GLFW

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    var cursor: ClientPositionMarker? = null
    var cursorMode: ICursorMode = UnboundedCursorMode()
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null

    companion object {
        const val UNBOUNDED_CURSOR_RANGE = 100.0
        val cursorModeBinding = KeyBinding("Baton Range Modifier", GLFW.GLFW_KEY_LEFT_CONTROL, SoundBounds.KEYBIND_CATEGORY)
    }
}
