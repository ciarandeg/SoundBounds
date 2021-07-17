package com.ciarandg.soundbounds.client.audio

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.exceptions.MissingAudioException
import com.ciarandg.soundbounds.client.exceptions.NoMetadataException
import com.ciarandg.soundbounds.client.exceptions.SongMetaMismatchException
import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.common.metadata.JsonSongMeta
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.sound.OggAudioStream
import net.minecraft.util.Identifier
import java.io.IOException

data class OggSong(
    val head: OggAudioStream?,
    val bodies: List<OggAudioStream>
) {
    constructor(meta: JsonSongMeta) : this(
        getHead(meta),
        meta.bodies.map { OggAudioStream(idToResource(it).inputStream) }
    )

    companion object {
        private const val RESOURCE_PATH = "sounds/music/"
        private const val OGG_EXT = ".ogg"

        fun fromID(songID: String): OggSong {
            val meta = ClientMeta.meta ?: throw NoMetadataException()
            val songMeta = meta.songs[songID] ?: throw SongMetaMismatchException(songID)
            return OggSong(songMeta)
        }

        private fun getHead(meta: JsonSongMeta): OggAudioStream? {
            if (meta.head == null) return null
            val stream = idToResource(meta.head).inputStream
            return OggAudioStream(stream)
        }

        private fun idToResource(songID: String) = try {
            val identifier = Identifier(SoundBounds.MOD_ID, RESOURCE_PATH + songID + OGG_EXT)
            GameInstance.getClient().resourceManager.getResource(identifier)
        } catch (e: IOException) {
            throw MissingAudioException(songID)
        }
    }
}
