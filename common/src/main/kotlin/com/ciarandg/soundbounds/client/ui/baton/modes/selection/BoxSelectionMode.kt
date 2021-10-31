package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree

class BoxSelectionMode : AbstractSelectionMode() {
    override fun getSelection() = ClientRegionBounds(
        marker1?.let { m1 ->
            marker2?.let { m2 ->
                BlockTree.fromBoxCorners(m1.getPos(), m2.getPos())
            }
        } ?: BlockTree()
    )
}
