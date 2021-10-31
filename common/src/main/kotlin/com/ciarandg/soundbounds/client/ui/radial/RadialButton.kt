package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

abstract class RadialButton(
    val onClick: () -> Unit,
    private val startAngle: Double,
    private val endAngle: Double,
    val hoverTexture: Identifier
) {
    fun isBisected(mousePos: PolarCoordinate): Boolean =
        with(mousePos) { return angle in startAngle..endAngle }
}
