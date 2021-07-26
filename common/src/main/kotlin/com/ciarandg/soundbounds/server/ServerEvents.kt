package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.event.events.PlayerEvent
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

object ServerEvents {
    fun register() {
        registerMVC()
        registerMetaHashCheck()
    }

    private fun registerMVC() {
        PlayerEvent.PLAYER_JOIN.register { player ->
            Controllers.putIfAbsent(player, PlayerController(player))
            sendWorldRegions(player)
        }
        PlayerEvent.CHANGE_DIMENSION.register { player, _, _ ->
            sendWorldRegions(player)
        }
    }

    private fun registerMetaHashCheck() {
        PlayerEvent.PLAYER_JOIN.register { player ->
            NetworkManager.sendToPlayer(player, SoundBounds.META_HASH_CHECK_S2C, PacketByteBuf(Unpooled.buffer()))
        }
    }

    private fun sendWorldRegions(player: ServerPlayerEntity) {
        val regions = WorldRegionState.get(player.serverWorld).getAllRegions().map { it.toPair() }
        NetworkManager.sendToPlayer(
            player,
            SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
            RegionUpdateMessageS2C.buildBufferS2C(true, regions)
        )
    }
}
