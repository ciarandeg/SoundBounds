package com.ciarandg.soundbounds.client.ui

object ClientPlayerModel {
    var batonMode = BatonMode.ADDITIVE
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null
    var uncommittedSelection = RegionSelection()
    var committedSelection = RegionSelection()
    var visualizationRegion: String? = null
}
