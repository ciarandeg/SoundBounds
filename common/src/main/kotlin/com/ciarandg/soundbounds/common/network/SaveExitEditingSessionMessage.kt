package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import com.ciarandg.soundbounds.server.ui.controller.WorldControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import kotlin.IllegalStateException

class SaveExitEditingSessionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) {
            val regionName = ClientPlayerModel.editingRegion ?: throw IllegalStateException("Must be editing a region")
            NetworkManager.sendToServer(SoundBounds.SAVE_EXIT_EDITING_SESSION_CHANNEL_C2S, buildBufferC2S(regionName))
        } else {
            val regionName = buf.readString(STR_LIMIT)

            val boundsListSize = buf.readInt()
            val bounds = mutableListOf<Int>()
            for (i in 1..boundsListSize) { bounds.add(buf.readInt()) }

            WorldControllers[ctx.player.world]?.saveExitEditingSession(regionName, BlockTree.deserialize(bounds), listOf())
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues

        fun buildBufferC2S(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)

            val bounds = ClientPlayerModel.committedSelection.blockTree.serialize()
            buf.writeInt(bounds.size)
            bounds.forEach { buf.writeInt(it) }
            return buf
        }
    }
}
