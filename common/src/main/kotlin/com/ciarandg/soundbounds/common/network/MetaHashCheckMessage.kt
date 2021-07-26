package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message for checking client metadata validity
class MetaHashCheckMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) NetworkManager.sendToServer(SoundBounds.META_HASH_CHECK_C2S, buildBufferC2S())
        else {
            val clientHash = buf.readInt()
            val serverHash = ServerMetaState.get().meta.hashCode()
            if (clientHash != serverHash) Controllers[ctx.player]?.notifyMetaMismatch()
                ?: throw IllegalStateException("Every player should have a controller")
        }
    }

    companion object {
        private fun buildBufferC2S(): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            val hash = ClientMeta.meta.hashCode()
            buf.writeInt(hash)
            return buf
        }
    }
}
