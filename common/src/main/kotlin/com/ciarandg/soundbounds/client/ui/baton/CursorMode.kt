package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState.Companion.DEFAULT_RADIUS_CURSOR_RANGE
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState.Companion.UNBOUNDED_CURSOR_RANGE

enum class CursorMode(var range: Double) {
    UNBOUNDED(UNBOUNDED_CURSOR_RANGE),
    RADIUS(DEFAULT_RADIUS_CURSOR_RANGE)
}
