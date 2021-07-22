package com.ciarandg.soundbounds.common.regions

import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.common.util.PlaylistType.SEQUENTIAL
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
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
    val volumes: MutableList<Pair<BlockPos, BlockPos>> = ArrayList()
) {
    fun toTag(): CompoundTag {
        val tag = CompoundTag()
        tag.putInt(Tag.PRIORITY.key, priority)
        tag.putString(Tag.PLAYLIST_TYPE.key, playlistType.toString())
        tag.put(Tag.PLAYLIST.key, playlistToTag(playlist))
        tag.put(Tag.VOLUMES.key, volumesToTag(volumes))
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
            CORNER1("c1"),
            CORNER2("c2"),
            CORNER_X("x"),
            CORNER_Y("y"),
            CORNER_Z("z"),
        }

        fun fromTag(tag: CompoundTag): RegionData {
            return RegionData(
                tag.getInt(Tag.PRIORITY.key),
                PlaylistType.valueOf(tag.getString(Tag.PLAYLIST_TYPE.key)),
                tagToPlaylist(tag.getList(Tag.PLAYLIST.key, 8)),
                tagToVolumeList(tag.getList(Tag.VOLUMES.key, 10))
            )
        }

        private fun tagToPlaylist(tag: ListTag) = tag.map {
            if (it !is StringTag) throw InvalidParameterException("Playlist entry tags must be strings")
            it.asString()
        }.toMutableList()

        private fun tagToVolumeList(tag: ListTag) = tag.map {
            if (it !is CompoundTag) throw InvalidParameterException("Volume tags must be compound")

            val corner1 = it[Tag.CORNER1.key]
            val corner2 = it[Tag.CORNER2.key]
            if (corner1 !is CompoundTag || corner2 !is CompoundTag)
                throw InvalidParameterException("Corner tags must be compound")

            Pair(tagToBlockPos(corner1), tagToBlockPos(corner2))
        }.toMutableList()

        private fun tagToBlockPos(tag: CompoundTag) = BlockPos(
            tag.getInt(Tag.CORNER_X.key),
            tag.getInt(Tag.CORNER_Y.key),
            tag.getInt(Tag.CORNER_Z.key)
        )
        private fun playlistToTag(playlist: List<String>): ListTag {
            val playlistTag = ListTag()
            playlistTag.addAll(playlist.map { StringTag.of(it) })
            return playlistTag
        }

        private fun volumesToTag(volumes: List<Pair<BlockPos, BlockPos>>): ListTag {
            val volumesTag = ListTag()
            volumesTag.addAll(
                volumes.map {
                    val boundingBox = CompoundTag()
                    boundingBox.put(Tag.CORNER1.key, blockPosToTag(it.first))
                    boundingBox.put(Tag.CORNER2.key, blockPosToTag(it.second))
                    boundingBox
                }
            )
            return volumesTag
        }

        private fun blockPosToTag(pos: BlockPos): CompoundTag {
            val t = CompoundTag()
            t.put(Tag.CORNER_X.key, IntTag.of(pos.x))
            t.put(Tag.CORNER_Y.key, IntTag.of(pos.y))
            t.put(Tag.CORNER_Z.key, IntTag.of(pos.z))
            return t
        }
    }
}
