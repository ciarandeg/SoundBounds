package com.ciarandg.soundbounds.client.ui.baton.modes.cursor

import kotlin.math.max
import kotlin.math.min

internal class BoundedCursorMode(range: Double = DEFAULT_RANGE) : ICursorMode {
    override var range: Double = range
        set(value) {
            field = min(max(value, MIN_RANGE), MAX_RANGE)
        }

    companion object {
        const val DEFAULT_RANGE = 10.0
        const val MAX_RANGE = 20.0
        const val MIN_RANGE = 0.0
    }
}
