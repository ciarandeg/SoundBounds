package com.ciarandg.soundbounds.client.ui

import me.shedaniel.architectury.event.events.TickEvent

object ClientPlayerModel {
    val batonState = PlayerBatonState()
    var uncommittedSelection = RegionSelection()
    var committedSelection = RegionSelection()
    var visualizationRegion: String? = null
}
