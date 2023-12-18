package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

// Two-way message to sync player pos markers
class PosMarkerUpdateMessage : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        val marker: PosMarker = buf.readEnumConstant(PosMarker.FIRST.javaClass)
        val pos = buf.readBlockPos()
        val showNotification = buf.readBoolean()

        if (ctx.player.world.isClient) {
            when (marker) {
                PosMarker.FIRST -> ClientPlayerModel.marker1 = pos
                PosMarker.SECOND -> ClientPlayerModel.marker2 = pos
            }
        } else {
            val player = ctx.player as ServerPlayerEntity
            PlayerControllers[player].setPosMarker(marker, pos, showNotification)
        }
    }

    companion object {
        fun buildBuffer(posMarker: PosMarker, pos: BlockPos, showNotificationC2S: Boolean = true): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeEnumConstant(posMarker)
            buf.writeBlockPos(pos)
            buf.writeBoolean(showNotificationC2S)
            return buf
        }
    }
}
