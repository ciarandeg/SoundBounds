package com.ciarandg.soundbounds.client.ui.radial.buttons

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.RadialButton

class RedoButton(startTheta: Float, endTheta: Float) : RadialButton(
    { SoundBounds.LOGGER.info("REDOING") }, startTheta, endTheta
) {
}