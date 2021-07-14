package com.ciarandg.soundbounds.common

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.ui.cli.SoundBoundsCommand
import com.ciarandg.soundbounds.common.ui.cli.argument.PTArgumentType
import com.ciarandg.soundbounds.server.ServerUtils
import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import me.shedaniel.architectury.event.events.CommandRegistrationEvent
import me.shedaniel.architectury.event.events.PlayerEvent
import me.shedaniel.architectury.networking.NetworkManager

object CommonEvents {
    fun register() {
        // Item registration
        SoundBounds.items.register()

        // Command registration
        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }

        // MVC setup
        PlayerEvent.PLAYER_JOIN.register {
            ServerUtils.playerControllers.putIfAbsent( it, PlayerController(it) )
        }

        // Metadata sync/update util registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.SYNC_METADATA_CHANNEL_C2S,
            MetadataSyncMessage()
        )
    }
}