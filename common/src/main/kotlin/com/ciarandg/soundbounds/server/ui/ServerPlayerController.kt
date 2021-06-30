package com.ciarandg.soundbounds.server.ui

import com.ciarandg.soundbounds.common.persistence.Region
import com.ciarandg.soundbounds.common.persistence.WorldState
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.ui.ServerPlayerView.FailureReason
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class ServerPlayerController(
    val owner: PlayerEntity,
    private val view: ServerPlayerView = CLIServerPlayerView(owner),
    private val model: PlayerModel = PlayerModel()
) {
    companion object {
        private const val DATA_KEY = "sb-data"
        private fun getWorldState(world: ServerWorld) =
            world.persistentStateManager.getOrCreate({ WorldState(DATA_KEY) }, DATA_KEY)
        private fun setWorldState(world: ServerWorld, state: WorldState) =
            world.persistentStateManager.set(state)
    }

    fun showNowPlaying() = view.showNowPlaying()
    fun setPosMarker(marker: PosMarker, pos: BlockPos) {
        when (marker) {
            PosMarker.FIRST -> model.marker1 = pos
            PosMarker.SECOND -> model.marker2 = pos
        }
        view.notifyPosMarkerSet(marker, pos)
    }

    fun listRegions(world: ServerWorld, radius: Int = -1) =
        view.showRegionList(getWorldState(world).getAllRegions().sortedBy { it.key })

    fun syncMetadata() {
        // TODO sync the metadata
        view.notifyMetadataSynced()
    }

    fun createRegion(world: ServerWorld, regionName: String, priority: Int) {
        val state = getWorldState(world)
        val m1 = model.marker1
        val m2 = model.marker2

        if (state.regionExists(regionName)) view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
        else if (m1 !=  null && m2 != null) {
            state.putRegion(regionName, Region(priority, bounds = listOf(Pair(m1, m2))))
            setWorldState(world, state)
            view.notifyRegionCreated(regionName, priority)
        } else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun destroyRegion(world: ServerWorld, regionName: String) {
        val state = getWorldState(world)
        val removed = state.removeRegion(regionName)
        if (removed == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            setWorldState(world, state)
            view.notifyRegionDestroyed(regionName)
        }
    }

    fun renameRegion(world: ServerWorld, from: String, to: String) {
        val state = getWorldState(world)
        val removed = state.removeRegion(from)
        when {
            removed == null -> view.notifyFailed(FailureReason.NO_SUCH_REGION)
            state.regionExists(to) -> view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
            else -> {
                state.putRegion(to, removed)
                setWorldState(world, state)
                view.notifyRegionRenamed(from, to)
            }
        }
    }

    fun showIfRegionsOverlap(firstRegion: String, secondRegion: String) {
        // TODO check if regions overlap
        view.notifyRegionOverlaps("", "", false)
    }

    fun showRegionInfo(world: ServerWorld, regionName: String) {
        val region = getWorldState(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionInfo(regionName, region)
    }

    fun setRegionPriority(world: ServerWorld, regionName: String, priority: Int) {
        val state = getWorldState(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldPriority = region.priority
            region.priority = priority
            state.putRegion(regionName, region)
            setWorldState(world, state)
            view.notifyRegionPrioritySet(regionName, oldPriority, priority)
        }
    }

    fun setRegionPlaylistType(world: ServerWorld, regionName: String, type: PlaylistType) {
        val state = getWorldState(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldType = region.playlistType
            region.playlistType = type
            state.putRegion(regionName, region)
            setWorldState(world, state)
            view.notifyRegionPlaylistTypeSet(regionName, oldType, type)
        }
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
    var curSongID: String? = null,
    var marker1: BlockPos? = null,
    var marker2: BlockPos? = null
)
