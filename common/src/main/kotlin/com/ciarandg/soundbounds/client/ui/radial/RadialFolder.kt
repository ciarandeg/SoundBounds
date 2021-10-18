package com.ciarandg.soundbounds.client.ui.radial

open class RadialFolder(
    val children: List<RadialButton>,
    startTheta: Float,
    endTheta: Float
) : RadialButton(
    {}
) {
    fun drawChildren() {
        // call each button's draw function
    }
}
