package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent

object AudioClientEventHandler {
    fun register() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register { GameMusicVolume.update() }
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register { RegionSwitcher.purge() }
    }
}
