package com.ciarandegroot.soundbounds.common.persistence

import com.ciarandegroot.soundbounds.common.util.PlaylistType
import com.ciarandegroot.soundbounds.common.util.PlaylistType.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos

data class Region(
    var priority: Int = 0,
    var playlistType: PlaylistType = SEQUENTIAL,
    val playlist: List<String> = ArrayList(),
    val bounds: List<Pair<BlockPos, BlockPos>> = ArrayList()
) {
    fun toTag(): CompoundTag {
        val tag = CompoundTag()

        tag.putInt("priority", priority)
        tag.putString("playlist-type", playlistType.toString())

        return tag
    }

    companion object {
        fun fromTag(tag: CompoundTag): Region {
            return Region(
                tag.getInt("priority"),
                PlaylistType.valueOf(tag.getString("playlist-type"))
            )
        }
    }
}