package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.networking.NetworkManager

object ClientEvents {
    fun register() {
        GuiEvent.INIT_POST.register { _, _, _ -> ClientMeta.update() }
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SYNC_METADATA_CHANNEL_S2C,
            MetadataSyncMessage()
        )
    }
}