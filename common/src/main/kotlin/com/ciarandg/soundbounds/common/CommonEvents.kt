package com.ciarandg.soundbounds.common

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.ui.cli.argument.PTArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.RegionArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.SongIDArgumentType
import com.ciarandg.soundbounds.common.ui.cli.command.SoundBoundsCommand
import me.shedaniel.architectury.event.events.CommandRegistrationEvent
import me.shedaniel.architectury.networking.NetworkManager

object CommonEvents {
    fun register() {
        // Item registration
        SoundBounds.items.register()

        // Command registration
        RegionArgumentType.register()
        SongIDArgumentType.register()
        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }

        // Metadata hash check registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.META_HASH_CHECK_C2S,
            MetaHashCheckMessage()
        )

        // Now-playing message registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.NOW_PLAYING_CHANNEL_C2S,
            NowPlayingMessage()
        )

        // Metadata sync/update util registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.SYNC_METADATA_CHANNEL_C2S,
            MetadataSyncMessage()
        )
    }
}
