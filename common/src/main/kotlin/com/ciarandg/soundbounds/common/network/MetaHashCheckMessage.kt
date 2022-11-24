package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

// Two-way message for checking client metadata validity
class MetaHashCheckMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) NetworkManager.sendToServer(SoundBounds.META_HASH_CHECK_C2S, buildBufferC2S())
        else {
            val clientHash = buf.readInt()
            val serverHash = ServerMetaState.get()?.meta?.hashCode()
            if (clientHash != serverHash) PlayerControllers[ctx.player as ServerPlayerEntity].notifyMetaMismatch()
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
