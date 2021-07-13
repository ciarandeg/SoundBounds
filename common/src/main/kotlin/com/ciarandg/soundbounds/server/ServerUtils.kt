package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import net.minecraft.entity.player.PlayerEntity

class ServerUtils {
    companion object {
        val playerControllers: HashMap<PlayerEntity, PlayerController> = HashMap()
    }
}