package com.ciarandg.soundbounds.client.audio

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.exceptions.EmptyPlaylistException
import com.ciarandg.soundbounds.client.exceptions.MissingAudioException
import com.ciarandg.soundbounds.client.exceptions.SongMetaMismatchException
import net.minecraft.client.sound.OggAudioStream
import net.minecraft.client.sound.StaticSound
import org.lwjgl.openal.AL10.AL_BUFFER
import org.lwjgl.openal.AL10.alGenSources
import org.lwjgl.openal.AL10.alSourcePlay
import org.lwjgl.openal.AL10.alSourceStop
import org.lwjgl.openal.AL10.alSourcei
import java.io.IOException
import java.lang.Exception

class PlaylistPlayer(playlist: List<String>) {
    private val source = alGenSources()
    private val oggPlaylist: List<OggSong> = playlist.mapNotNull { songID ->
        try { OggSong.fromID(songID) } catch (e: Exception) {
            SoundBounds.LOGGER.warn(when (e) {
                is SongMetaMismatchException -> "Requested song ${e.songID} has no local metadata"
                is MissingAudioException -> "Requested audio file does not exist: ${e.fileName}"
                else -> throw e
            })
            null
        }
    }

    fun start() {
        if (oggPlaylist.isEmpty()) throw EmptyPlaylistException()
        val ogg = oggPlaylist[0]
        val block = loadOgg(ogg.bodies[0])
        alSourcei(source, AL_BUFFER, block)
        alSourcePlay(source)
    }

    fun stop() {
        alSourceStop(source)
    }

    companion object {
        private fun loadOgg(ogg: OggAudioStream): Int {
            val buf = ogg.buffer // loads into memory here
            val sound = StaticSound(buf, ogg.format)
            val pointer = sound.takeStreamBufferPointer()
            if (pointer.isPresent) return pointer.asInt
            else throw IOException("Missing ogg data")
        }
    }
}
