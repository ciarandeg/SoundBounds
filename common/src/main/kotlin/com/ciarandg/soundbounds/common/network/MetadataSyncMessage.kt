package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.controller.Controllers
import com.google.gson.Gson
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf

// Two-way message for metadata supply. Server requests metadata from client, and client sends json metadata back.
class MetadataSyncMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient)
            NetworkManager.sendToServer(SoundBounds.SYNC_METADATA_CHANNEL_C2S, buildBufferC2S())
        else {
            val requester: PlayerEntity? = GameInstance.getServer()?.playerManager?.getPlayer(buf.readUuid())
            val requesterController = Controllers[requester]
            try {
                val state = ServerMetaState.get()
                state.meta = gson.fromJson(buf.readString(STR_LIMIT), JsonMeta::class.java)
                ServerMetaState.set(state)
                requesterController?.notifyMetadataSynced(true)
            } catch (e: Exception) {
                requesterController?.notifyMetadataSynced(false)
                throw e
            }
        }
    }

    companion object {
        private val gson = Gson()
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        private fun buildBufferC2S(): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeUuid(MinecraftClient.getInstance().player?.uuid) // used for success/failure message
            buf.writeString(gson.toJson(ClientMeta.meta ?: JsonMeta(), JsonMeta::class.java))
            return buf
        }
    }
}
