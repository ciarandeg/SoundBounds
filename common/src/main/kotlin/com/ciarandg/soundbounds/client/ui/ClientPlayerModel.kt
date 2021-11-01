package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode

object ClientPlayerModel {
    val batonState = PlayerBatonState()
    var uncommittedSelection = ClientRegionBounds()
    var committedSelection = ClientRegionBounds()
    var editingRegion: String? = null
    var visualizationRegion: String? = null

    fun commitSelection() {
        when (batonState.commitMode) {
            CommitMode.ADDITIVE -> committedSelection.blockTree.addAll(uncommittedSelection.blockTree)
            CommitMode.SUBTRACTIVE -> committedSelection.blockTree.removeAll(uncommittedSelection.blockTree)
        }
    }
}
