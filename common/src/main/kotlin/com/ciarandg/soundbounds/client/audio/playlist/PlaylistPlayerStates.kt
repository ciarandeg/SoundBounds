package com.ciarandg.soundbounds.client.audio.playlist

import com.ciarandg.soundbounds.client.audio.song.SongPlayer
import com.ciarandg.soundbounds.client.audio.song.types.OggSong
import com.ciarandg.soundbounds.client.options.SBClientOptions
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Future

interface PlaylistPlayerState {
    val owner: PlaylistPlayer
    fun tick(): PlaylistPlayerState
    fun cancel() // deletes buffers if necessary, so make sure no sources have them queued when this is called
}

class PlaylistPlayerStoppedState(override val owner: PlaylistPlayer) : PlaylistPlayerState {
    override fun tick(): PlaylistPlayerState = this
    override fun cancel() {} // problems that would lead to this happening already have their own warnings
    fun start() = PlaylistPlayerLoadingState(owner)
}

class PlaylistPlayerLoadingState(override val owner: PlaylistPlayer) : PlaylistPlayerState {
    private val loadingSong: Future<SongPlayer>

    init {
        val songToLoad = owner.dispenser.dispense()
        val ogg = OggSong.fromID(songToLoad)
        loadingSong = ForkJoinPool.commonPool().submit<SongPlayer> {
            SongPlayer(songToLoad, ogg)
        }
    }

    override fun tick(): PlaylistPlayerState =
        if (loadingSong.isDone) PlaylistPlayerPlayingState(owner, loadingSong.get())
        else this

    override fun cancel() {
        val timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (loadingSong.isDone) {
                        loadingSong.get().destroy()
                        timer.cancel()
                    }
                }
            },
            0, FREE_ATTEMPT_REPETITION_FREQ
        )
    }

    companion object {
        private const val FREE_ATTEMPT_REPETITION_FREQ: Long = 1000
    }
}

class PlaylistPlayerPlayingState(
    override val owner: PlaylistPlayer,
    private val song: SongPlayer
) : PlaylistPlayerState {
    init {
        song.start()
    }

    override fun tick(): PlaylistPlayerState =
        if (song.hasFinished()) PlaylistPlayerIdlingState(owner)
        else this

    override fun cancel() = song.destroy()

    fun currentSongID() = song.songID
}

class PlaylistPlayerIdlingState(override val owner: PlaylistPlayer) : PlaylistPlayerState {
    private val startTime = currentTime()

    override fun tick(): PlaylistPlayerState =
        if (currentTime() - startTime >= SBClientOptions.data.idleDuration) PlaylistPlayerLoadingState(owner)
        else this

    override fun cancel() {}

    private fun currentTime() = System.currentTimeMillis()
}
