package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.ui.baton.visualization.ClientVisualizationModel
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf

class VisualizeRegionMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        ClientVisualizationModel.visualizationRegion = buf.readString()
    }

    companion object {
        fun buildBuffer(regionName: String): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(regionName)
            return buf
        }
    }
}
