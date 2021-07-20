package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent
import me.shedaniel.architectury.networking.NetworkManager

object ClientEvents {
    fun register() {
        registerAudio()
        registerMetaHashCheck()
        registerNowPlaying()
        registerMetadata()
        registerRegionUpdate()
    }

    private fun registerAudio() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { GameMusicVolume.update() }
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { RegionSwitcher.purge() }
    }

    private fun registerMetaHashCheck() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.META_HASH_CHECK_S2C,
            MetaHashCheckMessage()
        )
    }

    private fun registerNowPlaying() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.NOW_PLAYING_CHANNEL_S2C,
            NowPlayingMessage()
        )
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
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.DESTROY_REGION_CHANNEL_S2C,
            RegionDestroyMessageS2C()
        )
    }
}
