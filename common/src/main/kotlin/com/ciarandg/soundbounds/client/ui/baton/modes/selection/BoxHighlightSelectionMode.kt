package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

class BoxHighlightSelectionMode : AbstractSelectionMode() {
    override fun getSelection() = ClientRegionBounds(
        marker1?.getPos()?.let { m1 ->
            marker2?.getPos()?.let { m2 ->
                val minPos = BlockPos(min(m1.x, m2.x), min(m1.y, m2.y), min(m1.z, m2.z))
                val maxPos = BlockPos(max(m1.x, m2.x), max(m1.y, m2.y), max(m1.z, m2.z))

                val committed = ClientPlayerModel.committedSelection.blockTree
                val committedCopy = committed.copy()
                committedCopy.retainAllWithinBounds(minPos, maxPos)
                committedCopy
            }
        } ?: BlockTree()
    )
}
