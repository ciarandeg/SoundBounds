package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import net.minecraft.util.math.BlockPos

object ClientPlayerModel {
    var marker1: BlockPos? = null
    var marker2: BlockPos? = null
    var uncommittedSelection = ClientRegionBounds(setOf())
    var committedSelection = ClientRegionBounds(setOf())
    var visualizationRegion: String? = null
}
