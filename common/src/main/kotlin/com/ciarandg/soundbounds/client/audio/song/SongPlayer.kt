package com.ciarandg.soundbounds.client.audio.song

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ClientTicker
import com.ciarandg.soundbounds.client.audio.AudioSource
import com.ciarandg.soundbounds.client.audio.song.types.LoadedSong
import com.ciarandg.soundbounds.client.audio.song.types.OggSong
import com.ciarandg.soundbounds.client.options.SBClientOptions
import com.ciarandg.soundbounds.common.network.NowPlayingMessage
import me.shedaniel.architectury.networking.NetworkManager
import java.lang.IllegalStateException
import java.util.Observable
import java.util.Observer

class SongPlayer(val songID: String, song: OggSong) : Observer {
    private val source = AudioSource()
    private val loadedSong = LoadedSong(song, BUFFER_DUR_MS)
    private val stepper = SongStepper(loadedSong, song.loop)
    private val isSoundscape = song.isSoundscape
    private var destroyed = false
    private var buffersQueued = 0

    init { ClientTicker.addObserver(this) }

    fun start() = synchronized(this) {
        when {
            destroyed -> throw IllegalStateException("Cannot start a destroyed SongPlayer")
            source.isPlaying() -> throw IllegalStateException("Playlist source shouldn't already be playing before starting")
            else -> {
                loadUntilCaughtUp()
                source.play()
                if (SBClientOptions.data.autoNowPlaying && !isSoundscape) notifyNowPlaying()
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) = synchronized(this) {
        if (source.isPlaying()) loadUntilCaughtUp()
    }

    fun destroy() = synchronized(this) {
        ClientTicker.deleteObserver(this)
        source.delete() // Must delete source before deallocating song buffers
        loadedSong.deallocate()
        destroyed = true
    }

    fun hasFinished() = source.isStopped()

    private fun loadUntilCaughtUp() {
        val nextToLoad = stepper.currentChunk() ?: return
        if (buffersQueued - source.buffersProcessed() > LOOKAHEAD) return
        source.queueBuffers(nextToLoad)
        buffersQueued += nextToLoad.size
        stepper.step()
        loadUntilCaughtUp()
    }

    private fun notifyNowPlaying() = NetworkManager.sendToServer(
        SoundBounds.NOW_PLAYING_CHANNEL_C2S, NowPlayingMessage.buildBufferC2S(songID)
    )

    companion object {
        private const val BUFFER_DUR_MS: Long = 1000 // max duration of each buffer
        private const val LOOKAHEAD = 4 // number of buffers in advance to queue the next part of song
    }
}
