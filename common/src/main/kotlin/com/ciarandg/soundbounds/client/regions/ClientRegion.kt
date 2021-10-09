package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.audio.playlist.PlaylistPlayer
import com.ciarandg.soundbounds.common.regions.RegionData

class ClientRegion(data: RegionData) {
    val priority = data.priority
    val bounds = ClientRegionBounds(data.bounds)
    val player = PlaylistPlayer(data.playlist, data.playlistType, data.queuePersistence)
}
