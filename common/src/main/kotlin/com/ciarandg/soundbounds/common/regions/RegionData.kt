package com.ciarandg.soundbounds.common.regions

import com.ciarandg.soundbounds.common.PlaylistType
import com.ciarandg.soundbounds.common.PlaylistType.SEQUENTIAL
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3i
import java.security.InvalidParameterException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class RegionData(
    var priority: Int = 0,
    var playlistType: PlaylistType = SEQUENTIAL,
    val playlist: MutableList<String> = ArrayList(),
    val volumes: MutableList<Pair<BlockPos, BlockPos>> = ArrayList(),
    var queuePersistence: Boolean = false
) {
    fun toTag(): NbtCompound {
        val tag = NbtCompound()
        tag.putInt(Tag.PRIORITY.key, priority)
        tag.putString(Tag.PLAYLIST_TYPE.key, playlistType.toString())
        tag.put(Tag.PLAYLIST.key, playlistToTag(playlist))
        tag.put(Tag.VOLUMES.key, volumesToTag(volumes))
        tag.putBoolean(Tag.PLAYLIST_PERSIST.key, queuePersistence)
        return tag
    }

    fun distanceFrom(pos: BlockPos): Double =
        volumes.map { Box(it.first, it.second) }.minOf { box ->
            fun clamp(pos: Double, min: Double, max: Double) = min(max(pos, min), max)
            val closestPoint = Vec3i(
                clamp(pos.x.toDouble(), box.minX, box.maxX),
                clamp(pos.y.toDouble(), box.minY, box.maxY),
                clamp(pos.z.toDouble(), box.minZ, box.maxZ),
            )
            val difference = pos.subtract(closestPoint)
            val squared = difference.x * difference.x + difference.y * difference.y + difference.z * difference.z
            sqrt(squared.toDouble())
        }

    companion object {
        private enum class Tag(val key: String) {
            PRIORITY("priority"),
            PLAYLIST_TYPE("playlist-type"),
            PLAYLIST("playlist"),
            VOLUMES("volumes"),
            PLAYLIST_PERSIST("playlist-persist"),
            CORNER1("c1"),
            CORNER2("c2"),
            CORNER_X("x"),
            CORNER_Y("y"),
            CORNER_Z("z"),
        }

        fun fromTag(tag: NbtCompound): RegionData {
            return RegionData(
                tag.getInt(Tag.PRIORITY.key),
                PlaylistType.valueOf(tag.getString(Tag.PLAYLIST_TYPE.key)),
                tagToPlaylist(tag.getList(Tag.PLAYLIST.key, 8)),
                tagToVolumeList(tag.getList(Tag.VOLUMES.key, 10)),
                tag.getBoolean(Tag.PLAYLIST_PERSIST.key)
            )
        }

        private fun tagToPlaylist(tag: NbtList) = tag.map {
            if (it !is NbtString) throw InvalidParameterException("Playlist entry tags must be strings")
            it.asString()
        }.toMutableList()

        private fun tagToVolumeList(tag: NbtList) = tag.map {
            if (it !is NbtCompound) throw InvalidParameterException("Volume tags must be compound")

            val corner1 = it[Tag.CORNER1.key]
            val corner2 = it[Tag.CORNER2.key]
            if (corner1 !is NbtCompound || corner2 !is NbtCompound)
                throw InvalidParameterException("Corner tags must be compound")

            Pair(tagToBlockPos(corner1), tagToBlockPos(corner2))
        }.toMutableList()

        private fun tagToBlockPos(tag: NbtCompound) = BlockPos(
            tag.getInt(Tag.CORNER_X.key),
            tag.getInt(Tag.CORNER_Y.key),
            tag.getInt(Tag.CORNER_Z.key)
        )
        private fun playlistToTag(playlist: List<String>): NbtList {
            val playlistTag = NbtList()
            playlistTag.addAll(playlist.map { NbtString.of(it) })
            return playlistTag
        }

        private fun volumesToTag(volumes: List<Pair<BlockPos, BlockPos>>): NbtList {
            val volumesTag = NbtList()
            volumesTag.addAll(
                volumes.map {
                    val boundingBox = NbtCompound()
                    boundingBox.put(Tag.CORNER1.key, blockPosToTag(it.first))
                    boundingBox.put(Tag.CORNER2.key, blockPosToTag(it.second))
                    boundingBox
                }
            )
            return volumesTag
        }

        private fun blockPosToTag(pos: BlockPos): NbtCompound {
            val t = NbtCompound()
            t.put(Tag.CORNER_X.key, NbtInt.of(pos.x))
            t.put(Tag.CORNER_Y.key, NbtInt.of(pos.y))
            t.put(Tag.CORNER_Z.key, NbtInt.of(pos.z))
            return t
        }
    }
}
