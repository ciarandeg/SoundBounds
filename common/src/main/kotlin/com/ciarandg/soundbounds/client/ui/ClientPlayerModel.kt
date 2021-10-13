package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.client.ui.baton.selection.RegionSelection

object ClientPlayerModel {
    val batonState = PlayerBatonState()
    var uncommittedSelection = RegionSelection()
    var committedSelection = RegionSelection()
    var editingRegion: String? = null
    var visualizationRegion: String? = null
}
