package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import me.shedaniel.architectury.event.events.PlayerEvent

object ServerEvents {
    fun register() {
        // MVC setup
        PlayerEvent.PLAYER_JOIN.register {
            ServerUtils.playerControllers.putIfAbsent(it, PlayerController(it))
        }
    }
}
