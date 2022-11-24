package com.ciarandg.soundbounds.common

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.CurrentRegionMessage
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.network.PosMarkerUpdateMessage
import com.ciarandg.soundbounds.common.ui.cli.argument.GroupNameArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.PTArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.RegionArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.SongIDArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.SongTagArgumentType
import com.ciarandg.soundbounds.common.ui.cli.command.SoundBoundsCommand
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.networking.NetworkManager

object CommonEvents {
    fun register() {
        // Command registration
        RegionArgumentType.register()
        SongIDArgumentType.register()
        GroupNameArgumentType.register()
        SongTagArgumentType.register()
        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }

        // Position marker update message registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.POS_MARKER_UPDATE_CHANNEL_C2S,
            PosMarkerUpdateMessage()
        )

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

        // Current-region message registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.CURRENT_REGION_CHANNEL_C2S,
            CurrentRegionMessage()
        )

        // Metadata sync/update util registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.SYNC_METADATA_CHANNEL_C2S,
            MetadataSyncMessage()
        )
    }
}
