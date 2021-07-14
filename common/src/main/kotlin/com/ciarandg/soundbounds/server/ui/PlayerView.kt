package com.ciarandg.soundbounds.server.ui

import com.ciarandg.soundbounds.common.regions.Region
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

interface PlayerView {
    val owner: PlayerEntity
    fun showNowPlaying()
    fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos)
    fun showRegionList(regions: List<Map.Entry<String, Region>>)
    fun notifyMetadataSynced()
    fun notifyMetadataSyncFailed()
    fun notifyRegionCreated(name: String, priority: Int)
    fun notifyRegionDestroyed(name: String)
    fun notifyRegionRenamed(from: String, to: String)
    fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) // TODO sub String for Region type
    fun showRegionInfo(regionName: String, region: Region)
    fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int)
    fun notifyRegionPlaylistTypeSet(name: String, from: PlaylistType, to: PlaylistType)
    fun notifyRegionVolumeAdded(regionName: String, volume: Pair<BlockPos, BlockPos>)
    fun notifyRegionVolumeRemoved(regionName: String, position: Int, volume: Pair<BlockPos, BlockPos>)
    fun showRegionVolumeList(regionName: String, volumes: List<Pair<BlockPos, BlockPos>>)
    fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int)
    fun showRegionContiguous(regionName: String)
    fun notifyFailed(reason: FailureReason)

    enum class FailureReason {
        POS_MARKERS_MISSING,
        NO_SUCH_REGION,
        REGION_NAME_CONFLICT,
        VOLUME_INDEX_OOB,
        REGION_MUST_HAVE_VOLUME,
        NO_METADATA_PRESENT,
        NO_SUCH_SONG,
        SONG_POS_OOB
    }
}
