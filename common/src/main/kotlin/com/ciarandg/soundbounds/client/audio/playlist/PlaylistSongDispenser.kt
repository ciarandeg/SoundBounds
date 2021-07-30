package com.ciarandg.soundbounds.client.audio.playlist

import com.ciarandg.soundbounds.client.exceptions.EmptyPlaylistException
import com.ciarandg.soundbounds.common.util.PlaylistType
import java.util.LinkedList
import java.util.Queue

class PlaylistSongDispenser(
    private val playlist: List<String>,
    private val type: PlaylistType
) {
    private val upcoming: Queue<String> = LinkedList()

    fun dispense(): String {
        if (playlist.isEmpty()) throw EmptyPlaylistException()
        if (upcoming.isEmpty()) generateCycle()
        return upcoming.poll()
    }

    fun scrap() = upcoming.clear()

    private fun generateCycle() = when (type) {
        PlaylistType.SEQUENTIAL -> playlist.forEach { upcoming.offer(it) }
        PlaylistType.SHUFFLED -> playlist.shuffled().forEach { upcoming.offer(it) }
    }
}
