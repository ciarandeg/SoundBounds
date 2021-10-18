package com.ciarandg.soundbounds.client.ui.radial

abstract class RadialButton(
    val onClick: () -> Unit,
) {
    fun draw(startTheta: Float, endTheta: Float, width: Int) {

    }
}