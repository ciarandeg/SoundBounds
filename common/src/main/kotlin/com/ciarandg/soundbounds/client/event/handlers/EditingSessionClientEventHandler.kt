package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.CancelEditingSessionMessage
import com.ciarandg.soundbounds.common.network.OpenEditingSessionMessageS2C
import com.ciarandg.soundbounds.common.network.SaveExitEditingSessionMessage
import me.shedaniel.architectury.networking.NetworkManager

object EditingSessionClientEventHandler {
    fun register() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.OPEN_EDITING_SESSION_CHANNEL_S2C,
            OpenEditingSessionMessageS2C()
        )

        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SAVE_EXIT_EDITING_SESSION_CHANNEL_S2C,
            SaveExitEditingSessionMessage()
        )

        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CANCEL_EDITING_SESSION_CHANNEL_S2C,
            CancelEditingSessionMessage()
        )
    }
}
