package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.RegionEntry
import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.exceptions.NoMetadataException
import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.client.regions.ClientWorldRegions
import com.ciarandg.soundbounds.common.regions.RegionData
import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import java.lang.RuntimeException

class RegionUpdateMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        val wipeExisting = buf.readBoolean()
        val regions = buf.readNbt() ?: throw RuntimeException("There should be a compound tag here")
        if (wipeExisting) wipeRegions()
        try {
            updateAll(regions)
        } catch (e: NoMetadataException) {
            SoundBounds.LOGGER.error(
                "No SoundBounds metadata available on client, refusing to sync regions." +
                    "Reload your resource pack and rejoin the server to sync."
            )
        }
    }

    private fun wipeRegions() {
        val oldSize = ClientWorldRegions.size
        ClientWorldRegions.clear()
        if (oldSize > 0) SoundBounds.LOGGER.info("Wiped local region data")
    }

    private fun updateAll(regions: NbtCompound) {
        val size = regions.size
        if (size == 0) return
        regions.keys.forEach { regionName ->
            val region = regions.getCompound(regionName)
            ClientWorldRegions[regionName] = ClientRegion(RegionData.fromTag(region))
        }
        SoundBounds.LOGGER.info("Synced $size " + if (size == 1) "region" else "regions")
    }

    companion object {
        fun buildBufferS2C(wipeExistingRegions: Boolean, regions: List<RegionEntry>): PacketByteBuf {
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeBoolean(wipeExistingRegions)
            val tag = NbtCompound()
            regions.forEach {
                tag.put(it.first, it.second.toTag())
            }
            buf.writeNbt(tag)
            return buf
        }
    }
}
