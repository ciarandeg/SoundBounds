package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.Vec3i
import kotlin.math.max
import kotlin.math.min

class ExtrudeSelectionMode : SculptingSelectionMode() {
    private val original = ClientSelectionController.getUncommitted().blockTree.copy()
    override fun getSelection(): ClientRegionBounds {
        val base = original.copy()

        marker1?.getPos()?.let { m1 ->
            marker2?.getPos()?.let { m2 ->
                val minX = min(m1.x, m2.x)
                val maxX = max(m1.x, m2.x)
                val minY = min(m1.y, m2.y)
                val maxY = max(m1.y, m2.y)
                val minZ = min(m1.z, m2.z)
                val maxZ = max(m1.z, m2.z)

                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        for (z in minZ..maxZ) {
                            base.addAll(BlockTree.translate(original, Vec3i(x - m1.x, y - m1.y, z - m1.z)))
                        }
                    }
                }
            }
        }

        return ClientRegionBounds(base)
    }
}
