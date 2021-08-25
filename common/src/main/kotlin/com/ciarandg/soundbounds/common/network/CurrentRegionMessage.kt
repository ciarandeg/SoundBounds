package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

// Two-way message to supply active region name to server
class CurrentRegionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient)
            NetworkManager.sendToServer(SoundBounds.CURRENT_REGION_CHANNEL_C2S, buildBufferC2S())
        else {
            val playerController = PlayerControllers[ctx.player as ServerPlayerEntity]
            val isRegion = buf.readBoolean()
            playerController.showCurrentRegion(if (isRegion) buf.readString(STR_LIMIT) else null)
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        private fun buildBufferC2S(): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())

            val nowPlaying = RegionSwitcher.currentRegionName()
            if (nowPlaying != null) {
                buf.writeBoolean(true)
                buf.writeString(nowPlaying)
            } else buf.writeBoolean(false)

            return buf
        }
    }
}
