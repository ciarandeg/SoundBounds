package com.ciarandegroot.soundbounds.server.`interface`

import com.ciarandegroot.cli.server.`interface`.PosMarker
import com.ciarandegroot.soundbounds.common.util.PlaylistType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos

class ServerPlayerController(
    val owner: PlayerEntity,
    private val view: ServerPlayerView,
    private val model: PlayerModel = PlayerModel()
) {
    fun showNowPlaying() = view.showNowPlaying()
    fun setPosMarker(marker: PosMarker, pos: BlockPos) {
        when (marker) {
            PosMarker.FIRST -> model.marker1 = pos
            PosMarker.SECOND -> model.marker2 = pos
        }
        view.notifyPosMarkerSet(marker, pos)
    }

    fun listRegions(radius: Int = -1, page: Int = 1) {
        // TODO fetch region data according to radius, pass it through
        view.showRegionList(listOf(""))
    }

    fun syncMetadata() {
        // TODO sync the metadata
        view.notifyMetadataSynced()
    }

    fun createRegion(regionName: String, priority: Int) {
        // TODO create the region
        view.notifyRegionCreated("", 0)
    }

    fun destroyRegion(regionName: String) {
        // TODO destroy the region
        view.notifyRegionDestroyed("")
    }

    fun renameRegion(from: String, to: String) {
        // TODO rename the region
        view.notifyRegionRenamed("", "")
    }

    fun showIfRegionsOverlap(firstRegion: String, secondRegion: String) {
        // TODO check if regions overlap
        view.notifyRegionOverlaps("", "", false)
    }

    fun showRegionInfo(regionName: String) {
        // TODO fetch region data from name
        view.showRegionInfo("")
    }

    fun setRegionPriority(regionName: String, priority: Int) {
        // TODO set region priority
        view.notifyRegionPrioritySet("", 0, 0)
    }

    fun setRegionPlaylistType(regionName: String, type: PlaylistType) {
        // TODO set region playlist type
        view.notifyRegionPlaylistTypeSet("", PlaylistType.SEQUENTIAL)
    }

    fun appendRegionPlaylistSong(regionName: String, songID: String) {
        // TODO append song to region playlist
        view.notifyRegionPlaylistSongAdded("", "", 0)
    }

    fun removeRegionPlaylistSong(regionName: String, songPosition: Int) {
        // TODO remove song from region playlist
        view.notifyRegionPlaylistSongRemoved("", "", 0)
    }

    fun insertRegionPlaylistSong(regionName: String, songID: String, songPosition: Int) {
        // TODO insert song into region playlist
        view.notifyRegionPlaylistSongAdded("", "", 0)
    }

    fun replaceRegionPlaylistSong(regionName: String, songPosition: Int, newSongID: String) {
        // TODO replace song in region playlist
        view.notifyRegionPlaylistSongReplaced("", "", "", 0)
    }

    fun checkRegionContiguous(regionName: String) {
        // TODO check if region is contiguous
        view.showRegionContiguous(regionName)
    }

    fun setCurrentSong(songID: String) {
    }
}

data class PlayerModel(
    var curSongID: String = "",
    var marker1: BlockPos = BlockPos(0, 0, 0),
    var marker2: BlockPos = BlockPos(0, 0, 0)
)
