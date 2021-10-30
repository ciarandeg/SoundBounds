package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree

class BoxHighlightSelectionMode : AbstractSelectionMode() {
    override fun getSelection() = ClientRegionBounds (
        marker1?.getPos()?.let { m1 ->
            marker2?.getPos()?.let { m2 ->
                val fullSelection = BlockTree.fromBoxCorners(m1, m2)
                fullSelection.intersection(ClientPlayerModel.committedSelection.blockTree)
            }
        } ?: BlockTree()
    )
}