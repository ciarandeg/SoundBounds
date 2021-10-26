package com.ciarandg.soundbounds.client.ui.baton.cursor

import kotlin.math.max
import kotlin.math.min

internal class CursorState {
    var isBounded = false
        private set
    val range: Double
        get() = if (isBounded) boundedRadius else RANGE_UNBOUNDED
    private var boundedRadius: Double = DEFAULT_RANGE_BOUNDED

    fun bind() {
        isBounded = true
    }

    fun unbind() {
        isBounded = false
    }

    fun incrementBoundedRadius(increment: Double) =
        setBoundedRadius(boundedRadius + increment)

    fun setBoundedRadius(radius: Double) {
        boundedRadius = min(max(radius, MIN_RANGE_BOUNDED), MAX_RANGE_BOUNDED)
    }

    companion object {
        const val RANGE_UNBOUNDED = 100.0
        const val DEFAULT_RANGE_BOUNDED = 10.0
        const val MAX_RANGE_BOUNDED = 20.0
        const val MIN_RANGE_BOUNDED = 0.0
    }
}
