package com.ciarandg.soundbounds.client.audio.playlist

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.AudioSource
import com.ciarandg.soundbounds.client.exceptions.NoMetadataException
import com.ciarandg.soundbounds.client.exceptions.SongMetaMismatchException
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.common.util.PlaylistType

class PlaylistPlayer(playlist: List<String>, type: PlaylistType) {
    var source = AudioSource()
        private set
    internal val dispenser = PlaylistSongDispenser(playlist, type)
    private var state: PlaylistPlayerState = PlaylistPlayerStoppedState(this)

    init {
        val meta = ClientMeta.meta ?: throw NoMetadataException()
        playlist.forEach {
            if (!meta.songs.containsKey(it))
                throw SongMetaMismatchException(it)
        }
    }

    fun start() = when (val currentState = state) {
        is PlaylistPlayerStoppedState -> state = currentState.start()
        else -> SoundBounds.LOGGER.error("Attempted to start a playlist that already started")
    }

    fun tick() {
        state = state.tick()
    }

    fun stop() {
        source.delete()
        state.cancel()
        source = AudioSource()
        state = PlaylistPlayerStoppedState(this)
    }

    fun currentSongID() = when (val currentState = state) {
        is PlaylistPlayerPlayingState -> currentState.currentSongID()
        else -> null
    }

    companion object {
        internal const val IDLE_DUR_MS: Long = 5000
    }
}