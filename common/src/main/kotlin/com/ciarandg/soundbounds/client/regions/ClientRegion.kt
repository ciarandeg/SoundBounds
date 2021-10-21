package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.audio.playlist.PlaylistPlayer
import com.ciarandg.soundbounds.common.regions.RegionData
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

data class ClientRegion(val data: RegionData) {
    val player = PlaylistPlayer(data.playlist, data.playlistType, data.queuePersistence)

    val blockSet = lazy {
        data.volumes.flatMap {
            val first = it.first
            val second = it.second

            val minX = min(first.x, second.x)
            val maxX = max(first.x, second.x)
            val minY = min(first.y, second.y)
            val maxY = max(first.y, second.y)
            val minZ = min(first.z, second.z)
            val maxZ = max(first.z, second.z)

            val blocks: MutableList<BlockPos> = ArrayList()
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        blocks.add(BlockPos(x, y, z))
                    }
                }
            }
            blocks
        }.toSet()
    }
}
