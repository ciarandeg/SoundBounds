package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.server.ui.ServerPlayerController
import net.minecraft.entity.player.PlayerEntity

class ServerUtils {
    companion object {
        val playerControllers: HashMap<PlayerEntity, ServerPlayerController> = HashMap()
    }
}