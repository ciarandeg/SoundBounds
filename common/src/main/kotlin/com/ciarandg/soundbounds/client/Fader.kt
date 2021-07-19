package com.ciarandg.soundbounds.client

import com.ciarandg.soundbounds.SoundBounds
import java.util.Observable
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.max

class Fader(
    private val postFadeCallback: () -> Unit
) : Observable() {
    private val state = State(MAX_GAIN, false)
    private val timer = Timer()
    private var incrementFadeTask = IncrementFadeTask(this)

    fun requestFade() {
        if (!state.isFading) startFade()
    }

    fun reset() {
        synchronized(state) {
            incrementFadeTask.cancel()
            incrementFadeTask = IncrementFadeTask(this)
            state.isFading = false
            state.gain = MAX_GAIN
            notifyGainChange()
        }
    }

    fun getGain() = state.gain

    private fun startFade() {
        synchronized(state) {
            state.isFading = true
            if (state.gain == MIN_GAIN) SoundBounds.LOGGER.warn("Attempting to fade from silence")
            timer.scheduleAtFixedRate(incrementFadeTask, 0, TICK_LENGTH_MS)
        }
    }

    private fun incrementFade() {
        synchronized(state) {
            state.gain = max(MIN_GAIN, state.gain - gainPerTick)
            notifyGainChange()
            if (state.gain == MIN_GAIN) {
                state.isFading = false
                reset()
                postFadeCallback()
            }
        }
    }

    private fun notifyGainChange() {
        setChanged()
        notifyObservers(state.gain)
    }

    companion object {
        private const val MAX_GAIN: Float = 1.0f
        private const val MIN_GAIN: Float = 0.0f
        private const val FADE_LENGTH_MS: Long = 2000
        private const val TICK_LENGTH_MS: Long = 20
        private val gainPerTick: Float =
            TICK_LENGTH_MS.toFloat() / FADE_LENGTH_MS.toFloat() * abs(MAX_GAIN - MIN_GAIN)
    }

    private data class State(var gain: Float, var isFading: Boolean)

    private class IncrementFadeTask(val owner: Fader) : TimerTask() {
        override fun run() = owner.incrementFade()
    }
}
