package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message to supply now-playing info
class NowPlayingMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient)
            NetworkManager.sendToServer(SoundBounds.NOW_PLAYING_CHANNEL_C2S, buildBufferC2S())
        else {
            val playerController =
                Controllers[ctx.player] ?: throw IllegalStateException("Every player should have a controller")
            val isPlaying = buf.readBoolean()
            playerController.showNowPlaying(if (isPlaying) buf.readString(STR_LIMIT) else null)
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        fun buildBufferC2S(): PacketByteBuf = buildBufferC2S(RegionSwitcher.currentSongID())

        fun buildBufferC2S(songID: String?): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())

            if (songID != null) {
                buf.writeBoolean(true)
                buf.writeString(songID)
            } else buf.writeBoolean(false)

            return buf
        }
    }
}
