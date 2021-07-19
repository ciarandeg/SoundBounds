package com.ciarandg.soundbounds.client.audio.song.types

import net.minecraft.client.sound.OggAudioStream
import net.minecraft.client.sound.StaticSound
import org.lwjgl.openal.AL10.AL_SIZE
import org.lwjgl.openal.AL10.alDeleteBuffers
import org.lwjgl.openal.AL10.alGetBufferi
import java.io.IOException
import javax.sound.sampled.AudioFormat

class LoadedSong(song: OggSong, private val bufferDur: Long) : Song<List<Int>> {
    override val head = song.head?.let { loadStream(it) }
    override val bodies = song.bodies.map { loadStream(it) }

    fun deallocate() {
        if (head != null) alDeleteBuffers(head.toIntArray())
        bodies.forEach { alDeleteBuffers(it.toIntArray()) }
    }

    private fun loadStream(stream: OggAudioStream): List<Int> {
        val out = ArrayList<Int>()
        while (true) {
            val bufSize = bytesPerBuffer(stream.format).toInt()
            val buf = stream.getBuffer(bufSize)
            val pointer = StaticSound(buf, stream.format).takeStreamBufferPointer()

            if (!pointer.isPresent) throw IOException("Missing ogg data")

            val block = pointer.asInt
            if (alGetBufferi(block, AL_SIZE) == 0) break
            out.add(block)
        }
        return out
    }

    private fun bytesPerBuffer(format: AudioFormat): Float {
        val bytesPerSecond = format.frameRate * format.frameSize
        return bytesPerSecond * bufferDur.toFloat() / 1000.0f
    }
}
