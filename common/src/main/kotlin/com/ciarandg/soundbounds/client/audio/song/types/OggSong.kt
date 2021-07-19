package com.ciarandg.soundbounds.client.audio.song.types

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

class OggSong(meta: JsonSongMeta) : Song<OggAudioStream> {
    override val head = meta.head?.let { OggAudioStream(idToResource(it).inputStream) }
    override val bodies = meta.bodies.map { OggAudioStream(idToResource(it).inputStream) }

    companion object {
        private const val RESOURCE_PATH = "sounds/music/"
        private const val OGG_EXT = ".ogg"

        fun fromID(songID: String): OggSong {
            val meta = ClientMeta.meta ?: throw NoMetadataException()
            val songMeta = meta.songs[songID] ?: throw SongMetaMismatchException(idPath(songID))
            return OggSong(songMeta)
        }

        private fun idToResource(songID: String) = try {
            val identifier = Identifier(SoundBounds.MOD_ID, idPath(songID))
            GameInstance.getClient().resourceManager.getResource(identifier)
        } catch (e: IOException) {
            throw MissingAudioException(songID)
        }

        private fun idPath(songID: String) = RESOURCE_PATH + songID + OGG_EXT
    }
}
