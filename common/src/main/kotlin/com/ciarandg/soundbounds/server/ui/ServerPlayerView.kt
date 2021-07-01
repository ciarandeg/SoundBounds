package com.ciarandg.soundbounds.server.ui

import com.ciarandg.soundbounds.common.persistence.Region
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.common.util.PlaylistType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

interface ServerPlayerView {
    val owner: PlayerEntity
    fun showNowPlaying()
    fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos)
    fun showRegionList(regions: List<Map.Entry<String, Region>>)
    fun notifyMetadataSynced()
    fun notifyRegionCreated(name: String, priority: Int)
    fun notifyRegionDestroyed(name: String)
    fun notifyRegionRenamed(from: String, to: String)
    fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) // TODO sub String for Region type
    fun showRegionInfo(regionName: String, region: Region)
    fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int)
    fun notifyRegionPlaylistTypeSet(name: String, from: PlaylistType, to: PlaylistType)
    fun showRegionVolumeList(regionName: String, volumes: List<Pair<BlockPos, BlockPos>>)
    fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int)
    fun showRegionContiguous(regionName: String)
    fun notifyFailed(reason: FailureReason)

    enum class FailureReason {
        POS_MARKERS_MISSING,
        NO_SUCH_REGION,
        REGION_NAME_CONFLICT
    }
}
