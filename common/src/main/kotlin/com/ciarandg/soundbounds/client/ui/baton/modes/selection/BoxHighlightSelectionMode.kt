package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds

class BoxHighlightSelectionMode : AbstractSelectionMode() {
    override fun getSelection(): ClientRegionBounds {
        return marker1?.getPos()?.let { m1 ->
            marker2?.getPos()?.let { m2 ->
                ClientRegionBounds()
            }
        } ?: ClientRegionBounds()
    }
}