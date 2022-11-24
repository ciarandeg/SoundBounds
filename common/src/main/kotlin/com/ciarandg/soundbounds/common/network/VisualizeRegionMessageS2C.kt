package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf

class VisualizeRegionMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        ClientPlayerModel.visualizationRegion = buf.readString()
    }

    companion object {
        fun buildBuffer(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)
            return buf
        }
    }
}
