package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import me.shedaniel.architectury.event.events.PlayerEvent
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.server.network.ServerPlayerEntity

object ServerEvents {
    fun register() {
        // MVC setup
        PlayerEvent.PLAYER_JOIN.register { player ->
            ServerUtils.playerControllers.putIfAbsent(player, PlayerController(player))
            sendWorldRegions(player)
        }
        PlayerEvent.CHANGE_DIMENSION.register { player, _, _ ->
            sendWorldRegions(player)
        }
    }

    private fun sendWorldRegions(player: ServerPlayerEntity) {
        val regions = WorldRegionState.get(player.serverWorld).getAllRegions().map { it.toPair() }
        NetworkManager.sendToPlayer(
            player,
            SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
            RegionUpdateMessageS2C.buildBuffer(true, regions)
        )
    }
}
