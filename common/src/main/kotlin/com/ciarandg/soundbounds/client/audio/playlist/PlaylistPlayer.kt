package com.ciarandg.soundbounds.client.audio.playlist

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.exceptions.NoMetadataException
import com.ciarandg.soundbounds.client.exceptions.SongMetaMismatchException
import com.ciarandg.soundbounds.common.PlaylistType

class PlaylistPlayer(playlist: List<String>, type: PlaylistType, private val queuePersist: Boolean) {
    internal val dispenser = PlaylistSongDispenser(playlist, type)
    private var state: PlaylistPlayerState = PlaylistPlayerStoppedState(this)

    fun start() = when (val currentState = state) {
        is PlaylistPlayerStoppedState -> try {
            state = currentState.start()
        } catch (e: NoMetadataException) {
            SoundBounds.LOGGER.warn("Attempted to start playlist, but there is no client-side metadata available")
        } catch (e: SongMetaMismatchException) {
            handleMetaMismatch(e)
        }
        else -> SoundBounds.LOGGER.error("Attempted to start a playlist that already started")
    }

    fun tick() = try {
        state = state.tick()
    } catch (e: SongMetaMismatchException) {
        handleMetaMismatch(e)
    }

    fun stop() {
        state.cancel()
        state = PlaylistPlayerStoppedState(this)
        if (!queuePersist) dispenser.scrap()
    }

    fun currentSongID() = when (val currentState = state) {
        is PlaylistPlayerPlayingState -> currentState.currentSongID()
        else -> null
    }

    private fun handleMetaMismatch(e: SongMetaMismatchException) =
        SoundBounds.LOGGER.warn("Attempted to play a song that has no metadata: ${e.songID}")
}
