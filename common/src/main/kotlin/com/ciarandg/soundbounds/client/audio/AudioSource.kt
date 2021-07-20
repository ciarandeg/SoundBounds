package com.ciarandg.soundbounds.client.audio

import com.ciarandg.soundbounds.client.Fader
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL10.AL_BUFFERS_PROCESSED
import org.lwjgl.openal.AL10.alDeleteSources
import org.lwjgl.openal.AL10.alGenSources
import org.lwjgl.openal.AL10.alGetSourcei
import org.lwjgl.openal.AL10.alSourcePlay
import org.lwjgl.openal.AL10.alSourceQueueBuffers
import java.lang.IllegalStateException
import java.util.Observable
import java.util.Observer

class AudioSource : Observer {
    private val pointer = alGenSources()
    private var faderGain = RegionSwitcher.fader.getGain()
    private var gameGain = GameMusicVolume.mixed()

    init {
        updateGain()
        RegionSwitcher.fader.addObserver(this)
        GameMusicVolume.addObserver(this)
    }

    fun delete() {
        RegionSwitcher.fader.deleteObserver(this)
        GameMusicVolume.deleteObserver(this)
        alDeleteSources(pointer)
    }

    fun play() = alSourcePlay(pointer)

    fun queueBuffers(buffers: List<Int>) = alSourceQueueBuffers(pointer, buffers.toIntArray())

    fun buffersProcessed() = alGetSourcei(pointer, AL_BUFFERS_PROCESSED)

    fun isPlaying() = alGetSourcei(pointer, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING
    fun isStopped() = alGetSourcei(pointer, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED

    private fun updateGain() = AL10.alSourcef(pointer, AL10.AL_GAIN, faderGain * gameGain)

    override fun update(o: Observable?, arg: Any?) {
        when (o) {
            is Fader -> faderGain = arg as Float
            is GameMusicVolume -> gameGain = arg as Float
            else -> throw IllegalStateException("Should only be getting updates from Fader or GameMusicVolume")
        }
        updateGain()
    }
}
