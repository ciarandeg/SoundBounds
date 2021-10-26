package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import com.ciarandg.soundbounds.client.ui.baton.modes.cursor.BoundedCursorMode
import com.ciarandg.soundbounds.client.ui.baton.modes.cursor.ICursorMode
import com.ciarandg.soundbounds.client.ui.baton.modes.cursor.UnboundedCursorMode
import net.minecraft.client.options.KeyBinding
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    var cursor: ClientPositionMarker? = null
    internal var cursorMode: ICursorMode = UnboundedCursorMode()
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null

    fun isCursorBounded() = cursorMode is BoundedCursorMode

    fun unbindCursor() {
        if (cursorMode !is UnboundedCursorMode)
            cursorMode = UnboundedCursorMode()
    }

    fun bindCursorToCurrentRadius(player: PlayerEntity, tickDelta: Float) {
        val currentRadius = cursor?.getPos()?.let {
            Vec3d.of(it).distanceTo(player.getCameraPosVec(tickDelta))
        } ?: BoundedCursorMode.DEFAULT_RANGE
        bindCursorToRadius(currentRadius)
    }

    fun incrementCursorRadius(increment: Double) {
        val mode = cursorMode
        if (mode !is BoundedCursorMode) return
        mode.range += increment
        cursorMode = mode
    }

    private fun bindCursorToRadius(radius: Double) {
        val currentMode = cursorMode
        val boundedMode: BoundedCursorMode =
            if (currentMode is BoundedCursorMode) currentMode else BoundedCursorMode()
        boundedMode.range = radius
        cursorMode = boundedMode
    }

    companion object {
        val cursorModeBinding = KeyBinding("Baton Range Modifier", GLFW.GLFW_KEY_LEFT_CONTROL, SoundBounds.KEYBIND_CATEGORY)
    }
}
