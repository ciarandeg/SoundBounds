package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

// start/end radii are [0, 1] since they're relative to texture size
abstract class RadialButton(
    val onClick: () -> Unit,
    private val startRadius: Double,
    private val endRadius: Double,
    private val startAngle: Double,
    private val endAngle: Double,
    val hoverTexture: Identifier
) {
    fun isHovered(mousePos: PolarCoordinate): Boolean =
        with(mousePos) { return radius in startRadius..endRadius && angle in startAngle..endAngle }
    fun isBisected(mousePos: PolarCoordinate): Boolean =
        with(mousePos) { return radius >= startRadius && angle in startAngle..endAngle }
}
