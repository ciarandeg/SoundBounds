package com.ciarandg.soundbounds.client.ui.baton.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState

internal class ClientSelectionModel {
    val batonState = PlayerBatonState()
    var uncommittedSelection = ClientRegionBounds()
    var committedSelection = ClientRegionBounds()
    var visualizationRegion: String? = null
}
