package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.client.options.SBClientOptions
import com.ciarandg.soundbounds.client.options.SBOptionsScreen
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.client.ui.radial.BatonMenu
import com.ciarandg.soundbounds.common.network.CancelEditingSessionMessage
import com.ciarandg.soundbounds.common.network.CommitSelectionMessage
import com.ciarandg.soundbounds.common.network.CreateRegionMessage
import com.ciarandg.soundbounds.common.network.CurrentRegionMessage
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.network.OpenEditingSessionMessageS2C
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.network.SaveExitEditingSessionMessage
import com.ciarandg.soundbounds.common.network.SetBatonModeMessageS2C
import com.ciarandg.soundbounds.common.network.VisualizeRegionMessageS2C
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient

object ClientEvents {
    fun register() {
        initializeOptions()
        registerTicker()
        registerAudio()
        registerBatonMenu()
        registerOptionsScreen()
        registerSetBatonMode()
        registerSelectionCommit()
        registerRegionCreate()
        registerOpenEditingSession()
        registerSaveExitEditingSession()
        registerCancelEditingSession()
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

    private fun registerBatonMenu() {
        KeyBindings.registerKeyBinding(BatonMenu.binding)

        TickEvent.PLAYER_POST.register {
            val client = MinecraftClient.getInstance()
            if (BatonMenu.binding.isPressed && client.currentScreen == null)
                SoundBounds.LOGGER.info("Opening baton menu ${System.currentTimeMillis()}")
        }
    }

    private fun registerOptionsScreen() {
        KeyBindings.registerKeyBinding(SBOptionsScreen.binding)

        TickEvent.PLAYER_POST.register {
            val client = MinecraftClient.getInstance()
            if (SBOptionsScreen.binding.isPressed && client.currentScreen == null)
                client.openScreen(SBOptionsScreen())
        }
    }

    private fun registerSetBatonMode() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SET_BATON_MODE_CHANNEL_S2C,
            SetBatonModeMessageS2C()
        )
    }

    private fun registerSelectionCommit() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.COMMIT_SELECTION_CHANNEL_S2C,
            CommitSelectionMessage()
        )
    }

    private fun registerRegionCreate() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CREATE_REGION_CHANNEL_S2C,
            CreateRegionMessage()
        )
    }

    private fun registerOpenEditingSession() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.OPEN_EDITING_SESSION_CHANNEL_S2C,
            OpenEditingSessionMessageS2C()
        )
    }

    private fun registerSaveExitEditingSession() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SAVE_EXIT_EDITING_SESSION_CHANNEL_S2C,
            SaveExitEditingSessionMessage()
        )
    }

    private fun registerCancelEditingSession() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CANCEL_EDITING_SESSION_CHANNEL_S2C,
            CancelEditingSessionMessage()
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
        GuiEvent.INIT_POST.register { _, _, _ -> ClientMeta.update() }
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
