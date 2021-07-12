package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.metadata.SBMeta
import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.ciarandg.soundbounds.server.ServerUtils
import com.google.gson.Gson
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.utils.Env
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import java.lang.RuntimeException

// Two-way message for metadata supply. Server requests metadata from client, and client sends json metadata back.
class MetadataSyncMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        when (Platform.getEnvironment()) {
            Env.CLIENT -> NetworkManager.sendToServer(SoundBounds.SYNC_METADATA_CHANNEL_C2S, buildBuffer())
            Env.SERVER -> {
                val requester: PlayerEntity? = GameInstance.getServer()?.playerManager?.getPlayer(buf.readUuid())
                val requesterController = ServerUtils.playerControllers[requester]
                try {
                    SBMeta.setServerMeta(gson.fromJson(buf.readString(STR_LIMIT), JsonMeta::class.java))
                    requesterController?.notifyMetadataSynced(true)
                } catch (e: Exception) {
                    requesterController?.notifyMetadataSynced(false)
                    throw e
                }
            }
            else -> throw RuntimeException("Environment is neither client nor server")
        }
    }

    companion object {
        private val gson = Gson()
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        private fun buildBuffer(): PacketByteBuf {
            assert(Platform.getEnvironment() == Env.CLIENT)
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeUuid(MinecraftClient.getInstance().player?.uuid) // used for success/failure message
            buf.writeString(gson.toJson(SBMeta.meta, JsonMeta::class.java))
            return buf
        }
    }
}