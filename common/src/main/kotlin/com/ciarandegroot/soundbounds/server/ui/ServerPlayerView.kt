package com.ciarandegroot.soundbounds.server.ui

import com.ciarandegroot.soundbounds.server.ui.cli.PosMarker
import com.ciarandegroot.soundbounds.common.util.PlaylistType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

interface ServerPlayerView {
    val owner: PlayerEntity
    fun showHelp()
    fun showNowPlaying()
    fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos)
    fun showRegionList(regions: List<String>) // TODO sub String for Region type
    fun notifyMetadataSynced()
    fun notifyRegionCreated(name: String, priority: Int)
    fun notifyRegionDestroyed(name: String)
    fun notifyRegionRenamed(from: String, to: String)
    fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) // TODO sub String for Region type
    fun showRegionInfo(region: String) // TODO sub String for Region type
    fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int)
    fun notifyRegionPlaylistTypeSet(name: String, type: PlaylistType)
    fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int)
    fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int)
    fun showRegionContiguous(regionName: String)
}
