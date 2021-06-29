package com.ciarandegroot.soundbounds.forge.common.network

import com.ciarandegroot.soundbounds.server.ServerUtils
import com.ciarandegroot.soundbounds.server.ui.cli.PosMarker
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos

class PosMarkerUpdateMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        val marker = buf.readEnumConstant(PosMarker.FIRST.javaClass)
        val pos = buf.readBlockPos()
        ServerUtils.playerControllers[ctx.player]?.setPosMarker(marker, pos)
    }

    companion object {
        fun buildBuffer(posMarker: PosMarker, pos: BlockPos): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeEnumConstant(posMarker)
            buf.writeBlockPos(pos)
            return buf
        }
    }
}