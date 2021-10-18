package com.ciarandg.soundbounds.client.ui.radial.buttons

import com.ciarandg.soundbounds.client.ui.radial.RadialButton

abstract class CommitModeButton(onClick: () -> Unit, startTheta: Float, endTheta: Float) : RadialButton(onClick, startTheta,
    endTheta
) {
}