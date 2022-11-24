package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.regions.ClientWorldRegions
import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf

class RegionDestroyMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        ClientWorldRegions.remove(buf.readString())
    }

    companion object {
        fun buildBuffer(regionName: String): PacketByteBuf =
            PacketByteBuf(Unpooled.buffer()).writeString(regionName)
    }
}
