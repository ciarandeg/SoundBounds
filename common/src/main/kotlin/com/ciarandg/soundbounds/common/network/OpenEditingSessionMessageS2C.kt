package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.regions.ClientWorldRegions
import com.ciarandg.soundbounds.client.ui.baton.editing.ClientEditingSessionModel
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf

class OpenEditingSessionMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext?) {
        val regionName = buf.readString()
        ClientEditingSessionModel.editingRegion = regionName
        ClientSelectionController.setCommitted(
            ClientWorldRegions[regionName]?.bounds?.blockTree
                ?: throw IllegalStateException("Region must exist in order for you to edit it")
        )
    }

    companion object {
        fun buildBuffer(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)
            return buf
        }
    }
}
