package com.ciarandg.soundbounds.client.audio.song

import com.ciarandg.soundbounds.client.audio.song.types.Song

class SongStepper<T>(
    private val song: Song<T>,
    private val loopBodies: Boolean
) {
    private var pastHead: Boolean = false
    private var bodyPos: Int = 0

    fun currentChunk(): T? = when {
        !pastHead && song.head != null -> song.head
        bodyPos < song.bodies.size -> song.bodies[bodyPos]
        else -> null
    }

    fun step(breakLoop: Boolean = false) {
        if (!pastHead) {
            pastHead = true
            if (song.head != null) return
        }
        if (loopBodies && !breakLoop) return
        else ++bodyPos
    }
}
