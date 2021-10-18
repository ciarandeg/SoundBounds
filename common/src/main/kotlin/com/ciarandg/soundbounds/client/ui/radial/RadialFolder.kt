package com.ciarandg.soundbounds.client.ui.radial

open class RadialFolder(
    val children: List<RadialButton> = listOf(),
    startTheta: Float,
    endTheta: Float
) : RadialButton(
    {}, startTheta, endTheta
) {
    fun drawChildren() {
        // call each button's draw function
    }
}