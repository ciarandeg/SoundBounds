package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.RegionEntry
import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.PlayerView
import net.minecraft.server.world.ServerWorld

open class PlaylistManager internal constructor(private val view: PlayerView) {
    fun appendRegionPlaylistSong(world: ServerWorld, regionName: String, songID: String) =
        checkNulls(world, regionName) { region, meta ->
            checkSongPresent(meta, songID) {
                region.playlist.add(songID)
                PlayerController.pushRegionToClients(world, RegionEntry(regionName, region))
                view.notifyRegionPlaylistSongAdded(regionName, songID, region.playlist.size)
            }
        }

    fun removeRegionPlaylistSong(world: ServerWorld, regionName: String, songPosition: Int) =
        checkNulls(world, regionName) { region, _ ->
            checkSongInBounds(region, songPosition) {
                val songID = region.playlist.removeAt(songPosition - 1)
                PlayerController.pushRegionToClients(world, RegionEntry(regionName, region))
                view.notifyRegionPlaylistSongRemoved(regionName, songID, songPosition)
            }
        }

    fun insertRegionPlaylistSong(world: ServerWorld, regionName: String, songID: String, songPosition: Int) =
        checkNulls(world, regionName) { region, meta ->
            checkSongPresent(meta, songID) {
                checkSongInBounds(region, songPosition) {
                    region.playlist.add(songPosition - 1, songID)
                    PlayerController.pushRegionToClients(world, RegionEntry(regionName, region))
                    view.notifyRegionPlaylistSongAdded(regionName, songID, songPosition)
                }
            }
        }

    fun replaceRegionPlaylistSong(world: ServerWorld, regionName: String, songPosition: Int, newSongID: String) =
        checkNulls(world, regionName) { region, meta ->
            checkSongPresent(meta, newSongID) {
                checkSongInBounds(region, songPosition) {
                    val oldSongID = region.playlist.set(songPosition - 1, newSongID)
                    PlayerController.pushRegionToClients(world, RegionEntry(regionName, region))
                    view.notifyRegionPlaylistSongReplaced(regionName, oldSongID, newSongID, songPosition)
                }
            }
        }

    private fun checkNulls(world: ServerWorld, regionName: String, work: (RegionData, JsonMeta) -> Unit) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        if (region == null) view.notifyFailed(PlayerView.FailureReason.NO_SUCH_REGION)
        else work(region, ServerMetaState.get().meta)
    }

    private fun checkSongPresent(meta: JsonMeta, songID: String, work: () -> Unit) =
        if (!meta.songs.containsKey(songID))
            view.notifyFailed(PlayerView.FailureReason.NO_SUCH_SONG)
        else work()

    private fun checkSongInBounds(data: RegionData, pos: Int, work: () -> Unit) =
        if (pos > data.playlist.size || pos < 1)
            view.notifyFailed(PlayerView.FailureReason.SONG_POS_OOB)
        else work()
}
