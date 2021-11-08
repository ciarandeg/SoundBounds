package com.ciarandg.soundbounds.common.event

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.CancelEditingSessionMessage
import com.ciarandg.soundbounds.common.network.CreateRegionMessage
import com.ciarandg.soundbounds.common.network.CurrentRegionMessage
import com.ciarandg.soundbounds.common.network.MetaHashCheckMessage
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import com.ciarandg.soundbounds.common.network.SaveExitEditingSessionMessage
import com.ciarandg.soundbounds.common.ui.cli.argument.GroupNameArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.PTArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.RegionArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.SongIDArgumentType
import com.ciarandg.soundbounds.common.ui.cli.argument.SongTagArgumentType
import com.ciarandg.soundbounds.common.ui.cli.command.SoundBoundsCommand
import me.shedaniel.architectury.event.events.CommandRegistrationEvent
import me.shedaniel.architectury.networking.NetworkManager

object CommonEvents {
    fun register() {
        // Command registration
        RegionArgumentType.register()
        SongIDArgumentType.register()
        GroupNameArgumentType.register()
        SongTagArgumentType.register()
        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }

        // Region creation registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.CREATE_REGION_CHANNEL_C2S,
            CreateRegionMessage()
        )

        // Editing session save/exit
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.SAVE_EXIT_EDITING_SESSION_CHANNEL_C2S,
            SaveExitEditingSessionMessage()
        )
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SoundBounds.CANCEL_EDITING_SESSION_CHANNEL_C2S,
            CancelEditingSessionMessage()
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
