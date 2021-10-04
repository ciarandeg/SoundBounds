package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.server.ui.controller.PlayerControllers
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

// Two-way message to sync player pos markers
// If sent from client, updates server and sends back to client
// If sent from server, updates client
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
            NetworkManager.sendToPlayer(player, SoundBounds.POS_MARKER_UPDATE_CHANNEL_S2C, buildBuffer(marker, pos))
        }
    }

    companion object {
        fun buildBuffer(posMarker: PosMarker, pos: BlockPos, showNotification: Boolean = true): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeEnumConstant(posMarker)
            buf.writeBlockPos(pos)
            buf.writeBoolean(showNotification)
            return buf
        }
    }
}
