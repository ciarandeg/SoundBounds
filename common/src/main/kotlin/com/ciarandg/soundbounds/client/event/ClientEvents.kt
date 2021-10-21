package com.ciarandg.soundbounds.client.event

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ClientTicker
import com.ciarandg.soundbounds.client.event.handlers.AudioClientEventHandler
import com.ciarandg.soundbounds.client.event.handlers.BatonClientEventHandler
import com.ciarandg.soundbounds.client.event.handlers.EditingSessionClientEventHandler
import com.ciarandg.soundbounds.client.event.handlers.MetadataClientEventHandler
import com.ciarandg.soundbounds.client.event.handlers.OptionsScreenClientEventHandler
import com.ciarandg.soundbounds.client.event.handlers.RegionClientEventHandler
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.client.options.SBClientOptions
import com.ciarandg.soundbounds.client.options.SBOptionsScreen
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient

object ClientEvents {
    fun register() {
        initializeOptions()
        registerTicker()
        AudioClientEventHandler.register()
        BatonClientEventHandler.register()
        OptionsScreenClientEventHandler.register()
        RegionClientEventHandler.register()
        EditingSessionClientEventHandler.register()
        MetadataClientEventHandler.register()
        registerNowPlaying()
    }

    private fun initializeOptions() {
        SBClientOptions // better to have it read the json here rather than in game loop
    }

    private fun registerTicker() = TickEvent.PLAYER_POST.register { if (it.world.isClient) ClientTicker.tick() }

    private fun registerNowPlaying() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.NOW_PLAYING_CHANNEL_S2C,
            NowPlayingMessage()
        )
    }
}
