package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.ClientPositionMarker
import com.ciarandg.soundbounds.client.regions.RegionSelection

object ClientPlayerModel {
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null
    var uncommittedSelection = RegionSelection()
    var committedSelection = RegionSelection()
    var visualizationRegion: String? = null
}
