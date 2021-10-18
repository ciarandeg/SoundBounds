package com.ciarandg.soundbounds.client.ui.radial.buttons

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.RadialButton

class RedoButton : RadialButton(
    { SoundBounds.LOGGER.info("REDOING") }
)
