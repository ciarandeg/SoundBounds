package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.RegionEntry
import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.PlaylistType
import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.regions.RegionAuditor
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason.NO_SUCH_REGION
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason.REGION_MUST_HAVE_VOLUME
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason.REGION_NAME_CONFLICT
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason.VOLUME_INDEX_OOB
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class WorldController(
    private val owner: ServerWorld
) {
    fun auditRegions(views: List<PlayerView>) = editWorldState { state ->
        val regions = state.getAllRegions()
        views.forEach {
            it.showAuditReport(
                RegionAuditor.auditEmptyPlaylists(regions),
                RegionAuditor.auditMissingMeta(regions)
            )
        }
    }

    fun createRegion(regionName: String, priority: Int, corner1: BlockPos, corner2: BlockPos, views: List<PlayerView>) =
        editWorldState { state ->
            if (!state.regionExists(regionName)) {
                val region = RegionEntry(regionName, RegionData(priority, bounds = mutableListOf(Pair(corner1, corner2))))
                state.putRegion(region.first, region.second)
                pushRegionToClients(owner, region)
                views.forEach { it.notifyRegionCreated(regionName, priority) }
            } else views.forEach { it.notifyFailed(REGION_NAME_CONFLICT) }
        }

    fun destroyRegion(regionName: String, views: List<PlayerView>) = editWorldState { state ->
        if (state.removeRegion(regionName) != null) {
            destroyRegionOnClients(owner, regionName)
            views.forEach { it.notifyRegionDestroyed(regionName) }
        } else views.forEach { it.notifyFailed(NO_SUCH_REGION) }
    }

    fun renameRegion(from: String, to: String, views: List<PlayerView>) = editWorldState { state ->
        val removed = state.removeRegion(from)
        if (state.regionExists(to)) views.forEach { it.notifyFailed(REGION_NAME_CONFLICT) }
        else when (removed) {
            null -> views.forEach { it.notifyFailed(NO_SUCH_REGION) }
            else -> {
                state.putRegion(to, removed)
                destroyRegionOnClients(owner, from)
                pushRegionToClients(owner, RegionEntry(to, removed))
                views.forEach { it.notifyRegionRenamed(from, to) }
            }
        }
    }

    fun setRegionPriority(regionName: String, priority: Int, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            val oldPriority = region.priority
            region.priority = priority
            pushRegionToClients(owner, RegionEntry(regionName, region))
            views.forEach { it.notifyRegionPrioritySet(regionName, oldPriority, priority) }
        }

    fun setRegionPlaylistType(regionName: String, type: PlaylistType, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            val oldType = region.playlistType
            region.playlistType = type
            pushRegionToClients(owner, RegionEntry(regionName, region))
            views.forEach { it.notifyRegionPlaylistTypeSet(regionName, oldType, type) }
        }

    fun addRegionVolume(regionName: String, corner1: BlockPos, corner2: BlockPos, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            val volume = Pair(corner1, corner2)
            region.bounds.add(volume)
            pushRegionToClients(owner, RegionEntry(regionName, region))
            views.forEach { it.notifyRegionVolumeAdded(regionName, volume) }
        }

    fun removeRegionVolume(regionName: String, index: Int, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            when {
                index < 0 || index >= region.bounds.size ->
                    views.forEach { it.notifyFailed(VOLUME_INDEX_OOB) }
                region.bounds.size == 1 ->
                    views.forEach { it.notifyFailed(REGION_MUST_HAVE_VOLUME) }
                else -> {
                    val volume = region.bounds.removeAt(index)
                    pushRegionToClients(owner, RegionEntry(regionName, region))
                    views.forEach { it.notifyRegionVolumeRemoved(regionName, index, volume) }
                }
            }
        }

    fun setRegionPlaylistQueuePersistence(regionName: String, queuePersist: Boolean, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            region.queuePersistence = queuePersist
            pushRegionToClients(owner, RegionEntry(regionName, region))
            views.forEach { it.notifyPlaylistPersistenceChanged(regionName, queuePersist) }
        }

    fun appendRegionPlaylistSong(regionName: String, songID: String, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, meta ->
            checkSongPresent(meta, songID, views) {
                region.playlist.add(songID)
                pushRegionToClients(owner, RegionEntry(regionName, region))
                views.forEach { it.notifyRegionPlaylistSongAdded(regionName, songID, region.playlist.size) }
            }
        }

    fun removeRegionPlaylistSong(regionName: String, songPosition: Int, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, _ ->
            checkSongInBounds(region, songPosition, views) {
                val songID = region.playlist.removeAt(songPosition - 1)
                pushRegionToClients(owner, RegionEntry(regionName, region))
                views.forEach { it.notifyRegionPlaylistSongRemoved(regionName, songID, songPosition) }
            }
        }

    fun insertRegionPlaylistSong(regionName: String, songID: String, songPosition: Int, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, meta ->
            checkSongPresent(meta, songID, views) {
                checkSongInBounds(region, songPosition, views) {
                    region.playlist.add(songPosition - 1, songID)
                    pushRegionToClients(owner, RegionEntry(regionName, region))
                    views.forEach { it.notifyRegionPlaylistSongAdded(regionName, songID, songPosition) }
                }
            }
        }

    fun replaceRegionPlaylistSong(regionName: String, songPosition: Int, newSongID: String, views: List<PlayerView>) =
        editExistingRegion(regionName, views) { region, meta ->
            checkSongPresent(meta, newSongID, views) {
                checkSongInBounds(region, songPosition, views) {
                    val oldSongID = region.playlist.set(songPosition - 1, newSongID)
                    pushRegionToClients(owner, RegionEntry(regionName, region))
                    views.forEach { it.notifyRegionPlaylistSongReplaced(regionName, oldSongID, newSongID, songPosition) }
                }
            }
        }

    private fun editExistingRegion(regionName: String, views: List<PlayerView>, work: (RegionData, JsonMeta) -> Unit) =
        editWorldState { state ->
            when (val region = state.getRegion(regionName)) {
                null -> views.forEach { it.notifyFailed(NO_SUCH_REGION) }
                else -> work(region, ServerMetaState.get().meta)
            }
        }

    private fun editWorldState(work: (WorldRegionState) -> Unit) = synchronized(owner) {
        val state = WorldRegionState.get(owner)
        work(state)
        WorldRegionState.set(owner, state)
    }

    private fun checkSongPresent(meta: JsonMeta, songID: String, views: List<PlayerView>, work: () -> Unit) =
        if (!meta.songs.containsKey(songID))
            views.forEach { it.notifyFailed(PlayerView.FailureReason.NO_SUCH_SONG) }
        else work()

    private fun checkSongInBounds(data: RegionData, pos: Int, views: List<PlayerView>, work: () -> Unit) =
        if (pos > data.playlist.size || pos < 1)
            views.forEach { it.notifyFailed(PlayerView.FailureReason.SONG_POS_OOB) }
        else work()

    companion object {
        private fun pushRegionToClients(world: ServerWorld, region: RegionEntry) =
            NetworkManager.sendToPlayers(
                world.players,
                SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
                RegionUpdateMessageS2C.buildBufferS2C(false, listOf(region))
            )
        private fun destroyRegionOnClients(world: ServerWorld, regionName: String) =
            NetworkManager.sendToPlayers(
                world.players,
                SoundBounds.DESTROY_REGION_CHANNEL_S2C,
                RegionDestroyMessageS2C.buildBuffer(regionName)
            )
    }
}
