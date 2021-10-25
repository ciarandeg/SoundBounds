package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState

object ClientPlayerModel {
    val batonState = PlayerBatonState()
    var uncommittedSelection = ClientRegionBounds()
    var committedSelection = ClientRegionBounds()
    var editingRegion: String? = null
    var visualizationRegion: String? = null
}
