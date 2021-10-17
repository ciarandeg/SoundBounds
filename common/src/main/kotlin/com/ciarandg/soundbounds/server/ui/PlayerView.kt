package com.ciarandg.soundbounds.server.ui

import com.ciarandg.soundbounds.client.ui.baton.CommitMode
import com.ciarandg.soundbounds.common.PlaylistType
import com.ciarandg.soundbounds.common.metadata.JsonSongMeta
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.common.ui.cli.Paginator
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

interface PlayerView {
    val owner: PlayerEntity
    fun showNowPlaying(nowPlaying: String)
    fun notifyMetaMismatch()
    fun notifyBatonModeSet(commitMode: CommitMode)
    fun showNoSongPlaying()
    fun showCurrentRegion(regionName: String?)
    fun showAuditReport(regionsWithEmptyPlaylists: Set<String>, regionsMissingMeta: Set<Pair<String, Set<String>>>)
    fun notifyCommittedToSelection()
    fun notifyEditingSessionOpened(regionName: String)
    fun notifyEditingSessionCanceled(regionName: String)
    fun notifySavedRegionEdit(regionName: String)
    fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos)
    fun notifyVisualizationRegionChanged(regionName: String)
    fun showRegionList(regions: List<Map.Entry<String, RegionData>>, paginator: Paginator)
    fun showRegionProximities(regions: List<Pair<Map.Entry<String, RegionData>, Double>>, paginator: Paginator)
    fun notifyMetadataSynced()
    fun notifyMetadataSyncFailed()
    fun notifyRegionCreated(name: String, priority: Int)
    fun notifyRegionDestroyed(name: String)
    fun notifyRegionRenamed(from: String, to: String)
    fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) // TODO sub String for Region type
    fun showRegionInfo(regionName: String, data: RegionData)
    fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int)
    fun notifyRegionPlaylistTypeSet(name: String, from: PlaylistType, to: PlaylistType)
    fun notifyRegionVolumeAdded(regionName: String, volume: Pair<BlockPos, BlockPos>)
    fun notifyRegionVolumeRemoved(regionName: String, position: Int, volume: Pair<BlockPos, BlockPos>)
    fun showRegionVolumeList(regionName: String, volumes: List<Pair<BlockPos, BlockPos>>, paginator: Paginator)
    fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int)
    fun showRegionContiguous(regionName: String)
    fun showSongList(songs: List<Pair<String, JsonSongMeta?>>, paginator: Paginator)
    fun showSongInfo(songID: String, song: JsonSongMeta)
    fun showGroupMembers(groupName: String, members: List<String>)
    fun notifyPlaylistPersistenceChanged(regionName: String, playlistPersist: Boolean)
    fun notifyFailed(reason: FailureReason)

    enum class FailureReason {
        EMPTY_SELECTION,
        NO_SUCH_REGION,
        NO_SUCH_GROUP,
        REGION_NAME_CONFLICT,
        VOLUME_INDEX_OOB,
        REGION_MUST_HAVE_VOLUME,
        NO_METADATA_PRESENT,
        NO_SUCH_SONG,
        GHOST_SONG,
        SONG_POS_OOB,
        PLAYER_NOT_SYNCER,
        PLAYER_ALREADY_EDITING_REGION,
        PLAYER_CURRENTLY_EDITING_DIFFERENT_REGION,
        OTHER_PLAYER_EDITING_REGION
    }
}
