package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

class GreyableRadialButton(
    onClickWhenActive: () -> Unit,
    val isGreyedOut: () -> Boolean,
    startAngle: Double,
    endAngle: Double,
    defaultTexture: Identifier,
    hoverTexture: Identifier,
    private val greyedTexture: Identifier
) : RadialButton(
    { if (!isGreyedOut()) onClickWhenActive() },
    startAngle, endAngle, defaultTexture, hoverTexture
) {
    override fun getCurrentTexture(mousePos: PolarCoordinate) =
        if (isGreyedOut()) greyedTexture else super.getCurrentTexture(mousePos)
}
