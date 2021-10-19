package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

abstract class RadialButton(
    val onClick: () -> Unit,
    private val startRadius: Double,
    private val startAngle: Double,
    private val endAngle: Double,
    val hoverTexture: Identifier
) {
    fun isHovered(mousePos: PolarCoordinate): Boolean =
        with(mousePos) { return radius >= startRadius && angle in startAngle..endAngle }
}
