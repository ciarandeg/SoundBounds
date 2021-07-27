package com.ciarandg.soundbounds.common.metadata

import java.net.URL

data class JsonMeta(
    val composers: Map<String, JsonComposerMeta> = emptyMap(),
    val groups: Map<String, List<String>> = emptyMap(),
    val songs: Map<String, JsonSongMeta> = emptyMap()
)

data class JsonComposerMeta(val promo: URL?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonComposerMeta

        if (promo?.toExternalForm() != other.promo?.toExternalForm()) return false

        return true
    }

    override fun hashCode() = promo?.toExternalForm()?.hashCode() ?: 0
}

data class JsonSongMeta(
    val title: String,
    val byGroup: Boolean,
    val artist: String,
    val featuring: List<String>?,
    val loop: Boolean,
    val head: String?,
    val bodies: List<String>,
    val tags: List<String>
)
