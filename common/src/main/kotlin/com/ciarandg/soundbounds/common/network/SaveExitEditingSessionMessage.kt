package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.server.ui.controller.WorldControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
import java.lang.IllegalStateException

class SaveExitEditingSessionMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        if (ctx.player.world.isClient) {
            val regionName = ClientPlayerModel.editingRegion ?: throw IllegalStateException("Must be editing a region")
            NetworkManager.sendToServer(SoundBounds.SAVE_EXIT_EDITING_SESSION_CHANNEL_C2S, buildBufferC2S(regionName))
        } else {
            val regionName = buf.readString(STR_LIMIT)

            val compound = buf.readCompoundTag() ?: throw IllegalStateException("There must be a compound tag here")
            val blockListTag = compound.getList(BLOCK_LIST_KEY, 10)
            val bounds = blockListTag.map { tag ->
                if (tag !is CompoundTag) throw IllegalStateException("Block list must consist solely of compound tags")
                RegionData.tagToBlockPos(tag)
            }.toSet()

            WorldControllers[ctx.player.world]?.saveExitEditingSession(regionName, bounds, listOf())
        }
    }

    companion object {
        private const val STR_LIMIT = 32767 // magic character limit to get around mapping issues
        private const val BLOCK_LIST_KEY = "block-list"

        fun buildBufferC2S(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)

            val compound = CompoundTag()
            val blockList = ListTag()
            with(ClientPlayerModel) {
                blockList.addAll(committedSelection.bounds.blockTree.map { RegionData.blockPosToTag(it) })
                committedSelection.reset()
            }
            compound.put(BLOCK_LIST_KEY, blockList)
            buf.writeCompoundTag(compound)

            return buf
        }
    }
}
