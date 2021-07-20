package com.ciarandg.soundbounds.client.audio.song.types

interface Song<T> {
    val head: T?
    val bodies: List<T?>
    val loop: Boolean
}
