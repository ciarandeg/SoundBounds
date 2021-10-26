package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.cursor.Cursor
import com.ciarandg.soundbounds.client.ui.baton.cursor.CursorState
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import net.minecraft.client.options.KeyBinding
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    val cursor = Cursor()
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null

    fun isCursorBounded() = cursor.state.isBounded

    fun unbindCursor() = cursor.state.unbind()

    fun bindCursorToCurrentRadius(player: PlayerEntity, tickDelta: Float) {
        val currentRadius = cursor.getPos()?.let {
            Vec3d.of(it).distanceTo(player.getCameraPosVec(tickDelta))
        } ?: CursorState.DEFAULT_RANGE_BOUNDED
        bindCursorToRadius(currentRadius)
    }

    fun incrementCursorRadius(increment: Double) =
        cursor.state.incrementBoundedRadius(increment)

    private fun bindCursorToRadius(radius: Double) {
        cursor.state.setBoundedRadius(radius)
        cursor.state.bind()
    }

    companion object {
        val cursorModeBinding = KeyBinding("Baton Range Modifier", GLFW.GLFW_KEY_LEFT_CONTROL, SoundBounds.KEYBIND_CATEGORY)
    }
}
