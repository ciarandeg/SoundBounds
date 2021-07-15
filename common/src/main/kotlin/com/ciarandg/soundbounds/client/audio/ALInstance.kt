package com.ciarandg.soundbounds.client.audio

import com.ciarandg.soundbounds.SoundBounds
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10

object ALInstance {
    private const val DEVICE_HANDLE: Long = 0xcd // arbitrary value
    private var device: Long? = null

    fun start() {
        val deviceName = ALC10.alcGetString(DEVICE_HANDLE, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER)
        device = ALC10.alcOpenDevice(deviceName)
        val alcCapabilities = ALC.createCapabilities(device ?: throw ConcurrentModificationException())
        AL.createCapabilities(alcCapabilities)
        SoundBounds.LOGGER.info("Created SoundBounds OpenAL device")
    }

    fun stop() {
        ALC10.alcCloseDevice(device ?: return)
        device = null
        SoundBounds.LOGGER.info("Destroyed SoundBounds OpenAL device")
    }
}
