package com.ciarandg.soundbounds.client.ui.radial

import kotlin.math.PI

abstract class MenuButtonGroup(private val buttons: List<RadialButton>) {
    fun getHoveredButton(mousePos: PolarCoordinate) = buttons.first { it.isBisected(mousePos) }

    enum class Angles(val rad: Double) {
        ZERO(0.0),
        EIGHTH(PI * 0.25),
        SIXTH(PI * 2.0 / 6.0),
        QUARTER(PI * 0.5),
        THIRD(PI * 2.0 / 3.0),
        HALF(PI),
        FULL(PI * 2.0)
    }
}
