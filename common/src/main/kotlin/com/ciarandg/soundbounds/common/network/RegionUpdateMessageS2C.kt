package com.ciarandg.soundbounds.common.network

import com.ciarandg.soundbounds.RegionEntry
import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientWorldRegions
import com.ciarandg.soundbounds.common.regions.Region
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.utils.Env
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import java.lang.RuntimeException

class RegionUpdateMessageS2C : NetworkManager.NetworkReceiver {
    override fun receive(buf: PacketByteBuf, ctx: NetworkManager.PacketContext) {
        val wipeExisting = buf.readBoolean()
        val regions = buf.readCompoundTag() ?: throw RuntimeException("There should be a compound tag here")
        if (wipeExisting) wipeRegions()
        updateAll(regions)
    }

    private fun wipeRegions() {
        val oldSize = ClientWorldRegions.size
        ClientWorldRegions.clear()
        if (oldSize > 0) SoundBounds.LOGGER.info("Wiped local region data")
    }

    private fun updateAll(regions: CompoundTag) {
        val size = regions.size
        if (size == 0) return
        regions.keys.forEach { regionName ->
            val region = regions.getCompound(regionName)
            ClientWorldRegions[regionName] = Region.fromTag(region)
        }
        SoundBounds.LOGGER.info("Synced $size " + if (size == 1) "region" else "regions")
    }

    companion object {
        fun buildBuffer(wipeExistingRegions: Boolean, regions: List<RegionEntry>): PacketByteBuf {
            assert(Platform.getEnvironment() == Env.SERVER)
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeBoolean(wipeExistingRegions)
            val tag = CompoundTag()
            regions.forEach {
                tag.put(it.first, it.second.toTag())
            }
            buf.writeCompoundTag(tag)
            return buf
        }
    }
}
