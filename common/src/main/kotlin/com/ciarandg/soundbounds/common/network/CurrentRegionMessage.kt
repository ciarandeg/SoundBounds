package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.utils.Env
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message to supply active region name to server
class CurrentRegionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        when (Platform.getEnvironment()) {
            Env.CLIENT -> NetworkManager.sendToServer(SoundBounds.CURRENT_REGION_CHANNEL_C2S, buildBuffer())
            Env.SERVER -> {
                val playerController =
                    Controllers[ctx.player] ?: throw IllegalStateException("Every player should have a controller")
                val isRegion = buf.readBoolean()
                playerController.showCurrentRegion(if (isRegion) buf.readString(STR_LIMIT) else null)
            }
            else -> throw RuntimeException("Environment is neither client nor server")
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        private fun buildBuffer(): PacketByteBuf {
            assert(Platform.getEnvironment() == Env.CLIENT)
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
