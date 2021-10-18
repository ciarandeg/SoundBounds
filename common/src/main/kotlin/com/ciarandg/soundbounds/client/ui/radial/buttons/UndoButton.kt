package com.ciarandg.soundbounds.client.ui.radial.buttons

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.RadialButton

class UndoButton(startTheta: Float, endTheta: Float) : RadialButton(
    { SoundBounds.LOGGER.info("UNDOING") }, startTheta, endTheta
) {}