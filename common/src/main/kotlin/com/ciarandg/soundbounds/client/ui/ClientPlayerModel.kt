package com.ciarandg.soundbounds.client.ui

import com.ciarandg.soundbounds.client.regions.RegionSelection
import net.minecraft.util.math.BlockPos

object ClientPlayerModel {
    var marker1: BlockPos? = null
    var marker2: BlockPos? = null
    var uncommittedSelection = RegionSelection()
    var committedSelection = RegionSelection()
    var visualizationRegion: String? = null
}
