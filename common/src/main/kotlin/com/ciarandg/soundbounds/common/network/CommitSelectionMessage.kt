package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

// Two-way message for committing a selection
class CommitSelectionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) {
            ClientPlayerModel.commitSelection()
            NetworkManager.sendToServer(SoundBounds.COMMIT_SELECTION_CHANNEL_C2S, buildBufferC2S())
        } else {
            val player: PlayerEntity = GameInstance.getServer()?.playerManager?.getPlayer(buf.readUuid())
                ?: throw IllegalStateException("There must be a valid player id here")
            val controller = PlayerControllers[player]
            controller?.notifyCommittedToSelection()
        }
    }

    companion object {
        private fun buildBufferC2S(): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            val player = MinecraftClient.getInstance().player ?: throw IllegalStateException("There must be a player on client")
            buf.writeUuid(player.uuid)
            return buf
        }
    }
}
