package com.ciarandegroot.soundbounds.server

import com.ciarandegroot.soundbounds.server.ui.ServerPlayerController
import net.minecraft.entity.player.PlayerEntity

class ServerUtils {
    companion object {
        val playerControllers: HashMap<PlayerEntity, ServerPlayerController> = HashMap()
    }
}