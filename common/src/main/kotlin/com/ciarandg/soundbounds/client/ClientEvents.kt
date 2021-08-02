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
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent
import me.shedaniel.architectury.event.events.client.ClientRawInputEvent
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.util.ActionResult
import org.lwjgl.glfw.GLFW

object ClientEvents {
    fun register() {
        initializeOptions()
        registerTicker()
        registerAudio()
        registerOptionsScreen()
        registerMetaHashCheck()
        registerNowPlaying()
        registerCurrentRegion()
        registerMetadata()
        registerRegionUpdate()
    }

    private fun initializeOptions() {
        SBClientOptions // better to have it read the json here rather than in game loop
    }

    private fun registerTicker() = TickEvent.PLAYER_POST.register { ClientTicker.tick() }

    private fun registerAudio() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { GameMusicVolume.update() }
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { RegionSwitcher.purge() }
    }

    private fun registerOptionsScreen() {
        ClientRawInputEvent.KEY_PRESSED.register { client, keyCode, scanCode, action, modifiers ->
            if (action == GLFW.GLFW_PRESS && keyCode == GLFW.GLFW_KEY_B) {
                when (client.currentScreen) {
                    null -> {
                        client.openScreen(SBOptionsScreen())
                        ActionResult.PASS
                    } is SBOptionsScreen -> {
                        client.openScreen(null)
                        ActionResult.PASS
                    } else -> ActionResult.CONSUME
                }
            } else ActionResult.CONSUME
        }
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
