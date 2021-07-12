package com.ciarandg.soundbounds.common.metadata

import java.net.URL

data class JsonMeta(
    val composers: Map<String, URL>,
    val groups: Map<String, List<String>>,
    val songs: Map<String, JsonSongMeta>
)

data class JsonSongMeta(
    val title: String,
    val byGroup: Boolean,
    val artist: String,
    val head: String?,
    val bodies: List<String>,
    val tags: List<String>
)