package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

open class RadialFolder(
    val children: List<RadialButton>,
    startRadius: Double,
    startAngle: Double,
    endAngle: Double,
    hoverTexture: Identifier
) : RadialButton({}, startRadius, startAngle, endAngle, hoverTexture) {
    fun drawChildren() {
        // call each button's draw function
    }
}
