package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.ALInstance
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent
import me.shedaniel.architectury.networking.NetworkManager

object ClientEvents {
    fun register() {
        registerAudio()
        registerMetadata()
        registerRegionUpdate()
    }

    private fun registerAudio() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { ALInstance.start() }
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { ALInstance.stop() }
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { GameMusicVolume.update() }
    }

    private fun registerMetadata() {
        GuiEvent.INIT_POST.register { _, _, _ -> ClientMeta.update() }
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SYNC_METADATA_CHANNEL_S2C,
            MetadataSyncMessage()
        )
    }

    private fun registerRegionUpdate() {
        TickEvent.PLAYER_POST.register { RegionSwitcher.update() }
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
            RegionUpdateMessageS2C()
        )
    }
}
