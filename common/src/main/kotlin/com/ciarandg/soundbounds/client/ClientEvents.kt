package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.client.options.SBClientOptions
import com.ciarandg.soundbounds.client.options.SBOptionsScreen
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.common.network.CurrentRegionMessage
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.network.PosMarkerUpdateMessage
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.network.VisualizeRegionMessageS2C
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientPlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.minecraft.client.MinecraftClient

object ClientEvents {
    fun register() {
        initializeOptions()
        registerTicker()
        registerAudio()
        registerOptionsScreen()
        registerPosMarkerUpdate()
        registerVisualizationRegionUpdate()
        registerMetaHashCheck()
        registerNowPlaying()
        registerCurrentRegion()
        registerMetadata()
        registerRegionUpdate()
    }

    private fun initializeOptions() {
        SBClientOptions // better to have it read the json here rather than in game loop
    }

    private fun registerTicker() = TickEvent.PLAYER_POST.register { if (it.world.isClient) ClientTicker.tick() }

    private fun registerAudio() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { GameMusicVolume.update() }
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { RegionSwitcher.purge() }
    }

    private fun registerOptionsScreen() {
        KeyMappingRegistry.register(SBOptionsScreen.binding)

        TickEvent.PLAYER_POST.register {
            val client = MinecraftClient.getInstance()
            if (SBOptionsScreen.binding.isPressed && client.currentScreen == null)
                client.setScreen(SBOptionsScreen())
        }
    }

    private fun registerPosMarkerUpdate() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.POS_MARKER_UPDATE_CHANNEL_S2C,
            PosMarkerUpdateMessage()
        )
    }

    private fun registerVisualizationRegionUpdate() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.VISUALIZE_REGION_CHANNEL_S2C,
            VisualizeRegionMessageS2C()
        )
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

    private fun registerCurrentRegion() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CURRENT_REGION_CHANNEL_S2C,
            CurrentRegionMessage()
        )
    }

    private fun registerMetadata() {
        ClientGuiEvent.INIT_POST.register { _, _ -> ClientMeta.update() }
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SYNC_METADATA_CHANNEL_S2C,
            MetadataSyncMessage()
        )
    }

    private fun registerRegionUpdate() {
        TickEvent.PLAYER_POST.register { if (it.world.isClient) RegionSwitcher.update() }
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
