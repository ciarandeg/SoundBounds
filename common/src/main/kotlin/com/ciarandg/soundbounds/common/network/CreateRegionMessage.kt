package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message for creating a region
class CreateRegionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient)
            NetworkManager.sendToServer(SoundBounds.CREATE_REGION_CHANNEL_C2S, buildBufferC2S(buf.readString(), buf.readInt()))
        else {
            val player: PlayerEntity = GameInstance.getServer()?.playerManager?.getPlayer(buf.readUuid()) ?: throw IllegalStateException("There must be a valid player id here")
            val controller = PlayerControllers[player] ?: throw IllegalStateException("There should already be a controller for this player")

            val regionName = buf.readString(STR_LIMIT)
            val regionPriority = buf.readInt()
            val bounds = buf.readString(STR_LIMIT)

            controller.createRegion(regionName, regionPriority, BlockTree.deserialize(bounds))
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        fun buildBufferS2C(regionName: String, regionPriority: Int): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)
            buf.writeInt(regionPriority)
            return buf
        }

        private fun buildBufferC2S(regionName: String, regionPriority: Int): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())

            buf.writeUuid(MinecraftClient.getInstance().player?.uuid ?: throw IllegalStateException("Client must have a player"))
            buf.writeString(regionName)
            buf.writeInt(regionPriority)

            val bounds = ClientPlayerModel.committedSelection.bounds.blockTree.serialize()
            buf.writeString(bounds)

            return buf
        }
    }
}
