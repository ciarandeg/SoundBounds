package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.Vec3i

class MoveSelectionMode : SculptingSelectionMode() {
    private val original = ClientPlayerModel.uncommittedSelection.blockTree

    override fun getSelection() =
        ClientRegionBounds(
            marker1?.getPos()?.let { m1 ->
                marker2?.getPos()?.let { m2 ->
                    val delta = Vec3i(m2.x - m1.x, m2.y - m1.y, m2.z - m1.z)
                    BlockTree.translate(original, delta)
                } ?: original
            } ?: original
        )
}
