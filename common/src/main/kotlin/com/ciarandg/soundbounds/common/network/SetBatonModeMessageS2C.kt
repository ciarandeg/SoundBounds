package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.ui.BatonMode
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf

// kind of bad practice because I'm keeping the BatonMode enum in the client package,
// but I plan on getting rid of this message soon anyways in favour of a graphical solution
class SetBatonModeMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        ClientPlayerModel.batonMode = BatonMode.valueOf(buf.readString())
    }
    companion object {
        fun buildBuffer(batonMode: BatonMode): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeString(batonMode.name)
            return buf
        }
    }
}
