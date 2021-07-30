package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.RegionEntry
import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.regions.RegionAuditor
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.PlayerModel
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.io.File

class PlayerController(
    val owner: PlayerEntity,
    private val view: PlayerView = CLIServerPlayerView(owner),
    private val model: PlayerModel = PlayerModel()
) : PlaylistManager(view) {
    val paginator = Paginator()

    fun showNowPlaying(player: ServerPlayerEntity) =
        NetworkManager.sendToPlayer(
            player,
            SoundBounds.NOW_PLAYING_CHANNEL_S2C,
            PacketByteBuf(Unpooled.buffer())
        )
    fun showNowPlaying(nowPlaying: String?) =
        if (nowPlaying == null) view.showNoSongPlaying()
        else view.showNowPlaying(nowPlaying)

    fun showCurrentRegion(player: ServerPlayerEntity) =
        NetworkManager.sendToPlayer(
            player,
            SoundBounds.CURRENT_REGION_CHANNEL_S2C,
            PacketByteBuf(Unpooled.buffer())
        )
    fun showCurrentRegion(regionName: String?) = view.showCurrentRegion(regionName)

    fun setPosMarker(marker: PosMarker, pos: BlockPos) {
        when (marker) {
            PosMarker.FIRST -> model.marker1 = pos
            PosMarker.SECOND -> model.marker2 = pos
        }
        view.notifyPosMarkerSet(marker, pos)
    }

    fun notifyMetaMismatch() = view.notifyMetaMismatch()

    fun auditRegions(world: ServerWorld) {
        val regions = WorldRegionState.get(world).getAllRegions()
        view.showAuditReport(
            RegionAuditor.auditEmptyPlaylists(regions),
            RegionAuditor.auditMissingMeta(regions)
        )
    }

    fun listRegions(world: ServerWorld) {
        view.showRegionList(WorldRegionState.get(world).getAllRegions().sortedBy { it.key }, paginator)
    }

    fun listRegionsWithinRadius(world: ServerWorld, radius: Int) {
        val allRegions = WorldRegionState.get(world).getAllRegions()
        val proximityPairs = allRegions.map { Pair(it, it.value.distanceFrom(owner.blockPos)) }
        val withinRadius = proximityPairs.sortedBy { it.second }.filter { it.second <= radius }
        view.showRegionProximities(withinRadius, paginator)
    }

    fun listRegionsContainingSong(world: ServerWorld, songID: String) {
        val allRegions = WorldRegionState.get(world).getAllRegions()
        val regionsContaining = allRegions.filter { it.value.playlist.contains(songID) }
        val songShouldExist = ServerMetaState.get().meta.songs.containsKey(songID)

        if (!songShouldExist) view.notifyFailed(
            if (regionsContaining.isEmpty()) {
                FailureReason.NO_SUCH_REGION
                return
            } else FailureReason.GHOST_SONG
        )
        view.showRegionList(regionsContaining, paginator)
    }

    fun listSongsContainingTag(tag: String) {
        val songMeta = ServerMetaState.get().meta.songs
        val songsContainingTag = songMeta.entries.sortedBy { it.key }.filter { it.value.tags.contains(tag) }.map { it.toPair() }
        view.showSongList(songsContainingTag, paginator)
    }

    fun listRegionPlaylistSongs(world: ServerWorld, regionName: String) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        val metaSongs = ServerMetaState.get().meta.songs
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showSongList(region.playlist.map { Pair(it, metaSongs[it]) }, paginator)
    }

    fun syncMetadata(player: ServerPlayerEntity) {
        val syncersFile = File(SYNCERS_PATH)
        val syncers = if (syncersFile.exists()) syncersFile.readLines().map { it.toLowerCase() } else null
        val playerName = player.name.asString().toLowerCase()
        if (syncers == null || syncers.contains(playerName)) NetworkManager.sendToPlayer(
            player,
            SoundBounds.SYNC_METADATA_CHANNEL_S2C,
            PacketByteBuf(Unpooled.buffer())
        ) else view.notifyFailed(FailureReason.PLAYER_NOT_SYNCER)
    }

    // this method breaks the MVC pattern a little, but is necessary since syncMetadata is networked
    fun notifyMetadataSynced(successful: Boolean) {
        if (successful) view.notifyMetadataSynced()
        else view.notifyMetadataSyncFailed()
    }

    fun createRegion(world: ServerWorld, regionName: String, priority: Int) {
        val state = WorldRegionState.get(world)
        val m1 = model.marker1
        val m2 = model.marker2

        if (state.regionExists(regionName)) view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
        else if (m1 != null && m2 != null) {
            val region = RegionEntry(regionName, RegionData(priority, volumes = mutableListOf(Pair(m1, m2))))
            state.putRegion(region.first, region.second)
            WorldRegionState.set(world, state)
            pushRegionToClients(world, region)
            view.notifyRegionCreated(regionName, priority)
        } else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun destroyRegion(world: ServerWorld, regionName: String) {
        val state = WorldRegionState.get(world)
        val removed = state.removeRegion(regionName)
        if (removed == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            WorldRegionState.set(world, state)
            destroyRegionOnClients(world, regionName)
            view.notifyRegionDestroyed(regionName)
        }
    }

    fun renameRegion(world: ServerWorld, from: String, to: String) {
        val state = WorldRegionState.get(world)
        val removed = state.removeRegion(from)
        when {
            removed == null -> view.notifyFailed(FailureReason.NO_SUCH_REGION)
            state.regionExists(to) -> view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
            else -> {
                state.putRegion(to, removed)
                WorldRegionState.set(world, state)
                destroyRegionOnClients(world, from)
                pushRegionToClients(world, RegionEntry(to, removed))
                view.notifyRegionRenamed(from, to)
            }
        }
    }

    fun showIfRegionsOverlap(firstRegion: String, secondRegion: String) {
        // TODO check if regions overlap
        view.notifyRegionOverlaps("", "", false)
    }

    fun showRegionInfo(world: ServerWorld, regionName: String) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionInfo(regionName, region)
    }

    fun listSongs() =
        view.showSongList(ServerMetaState.get().meta.songs.map { it.toPair() }.sortedBy { it.first }, paginator)

    fun showSongInfo(songID: String) {
        val song = ServerMetaState.get().meta.songs[songID]
        if (song == null) view.notifyFailed(FailureReason.NO_SUCH_SONG)
        else view.showSongInfo(songID, song)
    }

    fun setRegionPriority(world: ServerWorld, regionName: String, priority: Int) {
        val state = WorldRegionState.get(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldPriority = region.priority
            region.priority = priority
            state.putRegion(regionName, region)
            WorldRegionState.set(world, state)
            pushRegionToClients(world, RegionEntry(regionName, region))
            view.notifyRegionPrioritySet(regionName, oldPriority, priority)
        }
    }

    fun setRegionPlaylistType(world: ServerWorld, regionName: String, type: PlaylistType) {
        val state = WorldRegionState.get(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldType = region.playlistType
            region.playlistType = type
            state.putRegion(regionName, region)
            WorldRegionState.set(world, state)
            pushRegionToClients(world, RegionEntry(regionName, region))
            view.notifyRegionPlaylistTypeSet(regionName, oldType, type)
        }
    }

    fun addRegionVolume(world: ServerWorld, regionName: String) {
        val state = WorldRegionState.get(world)
        val region = state.getRegion(regionName)
        val m1 = model.marker1
        val m2 = model.marker2

        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else if (m1 != null && m2 != null) {
            val volume = Pair(m1, m2)
            region.volumes.add(volume)
            WorldRegionState.set(world, state)
            pushRegionToClients(world, RegionEntry(regionName, region))
            view.notifyRegionVolumeAdded(regionName, volume)
        } else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun removeRegionVolume(world: ServerWorld, regionName: String, index: Int) {
        val state = WorldRegionState.get(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else if (index < 0 || index >= region.volumes.size) view.notifyFailed(FailureReason.VOLUME_INDEX_OOB)
        else if (region.volumes.size == 1) view.notifyFailed(FailureReason.REGION_MUST_HAVE_VOLUME)
        else {
            val volume = region.volumes.removeAt(index)
            pushRegionToClients(world, RegionEntry(regionName, region))
            view.notifyRegionVolumeRemoved(regionName, index, volume)
        }
    }

    fun listRegionVolumes(world: ServerWorld, regionName: String) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionVolumeList(regionName, region.volumes, paginator)
    }

    fun checkRegionContiguous(world: ServerWorld, regionName: String) {
        // TODO check if region is contiguous
        view.showRegionContiguous(regionName)
    }

    fun showGroupInfo(groupName: String) {
        val members = ServerMetaState.get().meta.groups[groupName]
        if (members == null) view.notifyFailed(FailureReason.NO_SUCH_GROUP)
        else view.showGroupMembers(groupName, members)
    }

    companion object {
        const val SYNCERS_PATH = "./config/${SoundBounds.MOD_ID}/syncers.txt"
        internal fun pushRegionToClients(world: ServerWorld, region: RegionEntry) =
            NetworkManager.sendToPlayers(
                world.players,
                SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
                RegionUpdateMessageS2C.buildBufferS2C(false, listOf(region))
            )
        internal fun destroyRegionOnClients(world: ServerWorld, regionName: String) =
            NetworkManager.sendToPlayers(
                world.players,
                SoundBounds.DESTROY_REGION_CHANNEL_S2C,
                RegionDestroyMessageS2C.buildBuffer(regionName)
            )
    }
}
