package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.editing.ClientEditingSessionModel
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import com.ciarandg.soundbounds.server.ui.controller.WorldControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf

class CancelEditingSessionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) {
            val wasEditing = ClientEditingSessionModel.editingRegion ?: throw IllegalStateException("Attempted to cancel a nonexistent session")
            ClientEditingSessionModel.editingRegion = null
            ClientSelectionController.clearCommitted()
            NetworkManager.sendToServer(SoundBounds.CANCEL_EDITING_SESSION_CHANNEL_C2S, buildBufferC2S(wasEditing))
        } else {
            val wasEditing = buf.readString(STR_LIMIT)
            WorldControllers[ctx.player.world]?.cancelEditingSession(
                wasEditing,
                listOf(PlayerControllers[ctx.player]?.view ?: throw IllegalStateException("Player should already have a controller"))
            )
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues
        fun buildBufferC2S(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)
            return buf
        }
    }
}
