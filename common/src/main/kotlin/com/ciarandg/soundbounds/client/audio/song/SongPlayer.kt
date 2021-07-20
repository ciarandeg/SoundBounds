package com.ciarandg.soundbounds.client.audio.song

import com.ciarandg.soundbounds.client.audio.AudioSource
import com.ciarandg.soundbounds.client.audio.song.types.LoadedSong
import com.ciarandg.soundbounds.client.audio.song.types.OggSong
import java.lang.IllegalStateException
import java.lang.Long.max
import java.util.Timer
import java.util.TimerTask

class SongPlayer(val songID: String, private val source: AudioSource, song: OggSong) {
    private val loadedSong = LoadedSong(song, BUFFER_DUR_MS)
    private val stepper = SongStepper(loadedSong, song.loop)
    private val timer = Timer()
    private var destroyed = false

    fun start() = synchronized(this) {
        if (!destroyed) queueStepWait()
        else throw IllegalStateException("Cannot start a destroyed SongPlayer")
    }

    fun destroy() = synchronized(this) {
        timer.cancel()
        loadedSong.deallocate()
        destroyed = true
    }

    private fun queueStepWait(makeup: Long = 0): Unit = synchronized(this) {
        fun scheduleRecursion(delay: Long) = timer.schedule(
            object : TimerTask() { override fun run() = queueStepWait(lookaheadDur()) },
            delay
        )

        val current = stepper.currentChunk() ?: return
        source.queueBuffers(current)
        stepper.step()
        val delay = max(0L, chunkDur(current) - lookaheadDur() + makeup)
        scheduleRecursion(delay)
        if (!source.isPlaying()) source.play()
    }

    companion object {
        private const val BUFFER_DUR_MS: Long = 1000 // max duration of each buffer
        private const val LOOKAHEAD = 4 // number of buffers in advance to queue the next part of song

        private fun chunkDur(bufCount: Int): Long = BUFFER_DUR_MS * bufCount
        private fun chunkDur(chunk: List<Int>): Long = chunkDur(chunk.size)
        private fun lookaheadDur() = chunkDur(LOOKAHEAD)
    }
}
