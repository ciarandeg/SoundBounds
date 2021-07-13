package com.ciarandg.soundbounds.common.metadata

import java.net.URL

data class JsonMeta(
    val composers: Map<String, URL> = emptyMap(),
    val groups: Map<String, List<String>> = emptyMap(),
    val songs: Map<String, JsonSongMeta> = emptyMap()
)

data class JsonSongMeta(
    val title: String,
    val byGroup: Boolean,
    val artist: String,
    val head: String?,
    val bodies: List<String>,
    val tags: List<String>
)