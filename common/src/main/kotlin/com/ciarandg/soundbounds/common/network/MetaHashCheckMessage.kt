package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.utils.Env
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message for checking client metadata validity
class MetaHashCheckMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        when (Platform.getEnvironment()) {
            Env.CLIENT -> NetworkManager.sendToServer(
                SoundBounds.META_HASH_CHECK_C2S,
                buildBuffer()
            )
            Env.SERVER -> {
                val clientHash = buf.readInt()
                // SoundBounds.LOGGER.info("PULLED $clientHash from CLIENT BUFFER")
                val serverHash = ServerMetaState.get().meta.hashCode()
                // SoundBounds.LOGGER.info("FOUND $serverHash on SERVER")
                if (clientHash != serverHash) Controllers[ctx.player]?.notifyMetaMismatch()
                    ?: throw IllegalStateException("Every player should have a controller")
            }
            else -> throw RuntimeException("Environment is neither client nor server")
        }
    }

    companion object {
        private fun buildBuffer(): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            val hash = ClientMeta.meta.hashCode()
            buf.writeInt(hash)
            // SoundBounds.LOGGER.info("WROTE $hash to CLIENT BUFFER")
            return buf
        }
    }
}
