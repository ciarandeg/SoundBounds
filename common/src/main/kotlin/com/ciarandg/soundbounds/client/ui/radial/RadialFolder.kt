package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

open class RadialFolder(
    val children: List<RadialButton>,
    startRadius: Double,
    endRadius: Double,
    startAngle: Double,
    endAngle: Double,
    hoverTexture: Identifier
) : RadialButton({}, startRadius, endRadius, startAngle, endAngle, hoverTexture)
