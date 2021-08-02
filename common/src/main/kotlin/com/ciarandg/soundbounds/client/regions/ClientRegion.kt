package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.audio.playlist.PlaylistPlayer
import com.ciarandg.soundbounds.common.regions.RegionData

data class ClientRegion(val data: RegionData) {
    val player = PlaylistPlayer(data.playlist, data.playlistType, data.queuePersistence)
}
