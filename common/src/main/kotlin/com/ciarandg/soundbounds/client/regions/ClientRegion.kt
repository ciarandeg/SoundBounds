package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.client.audio.PlaylistPlayer
import com.ciarandg.soundbounds.common.regions.Region

data class ClientRegion(val data: Region) {
    val player = PlaylistPlayer(data.playlist)
}
