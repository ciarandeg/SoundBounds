package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

open class RadialFolder(
    val children: List<RadialButton>,
    startAngle: Double,
    endAngle: Double,
    hoverTexture: Identifier
) : RadialButton({}, startAngle, endAngle, hoverTexture)
