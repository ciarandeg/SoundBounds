package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.PosMarkerUpdateMessage
import com.ciarandg.soundbounds.common.network.VisualizeRegionMessageS2C
import com.ciarandg.soundbounds.common.regions.WorldRegionState
import com.ciarandg.soundbounds.common.ui.cli.Paginator
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.PlayerModel
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import java.io.File

class PlayerController(
    val owner: ServerPlayerEntity,
    val view: PlayerView = CLIServerPlayerView(owner),
    private val model: PlayerModel = PlayerModel()
) {
    val paginator = Paginator()
    private val world: ServerWorld
        get() = owner.getWorld()

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

    fun setPosMarker(marker: PosMarker, pos: BlockPos, showNotification: Boolean = true) {
        when (marker) {
            PosMarker.FIRST -> model.marker1 = pos
            PosMarker.SECOND -> model.marker2 = pos
        }
        NetworkManager.sendToPlayer(owner, SoundBounds.POS_MARKER_UPDATE_CHANNEL_S2C, PosMarkerUpdateMessage.buildBuffer(marker, pos))
        if (showNotification) view.notifyPosMarkerSet(marker, pos)
    }

    fun setVisualizingRegion(regionName: String) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        if (region != null) {
            NetworkManager.sendToPlayer(owner, SoundBounds.VISUALIZE_REGION_CHANNEL_S2C, VisualizeRegionMessageS2C.buildBuffer(regionName))
            view.notifyVisualizationRegionChanged(regionName)
        } else view.notifyFailed(FailureReason.NO_SUCH_REGION)
    }

    fun notifyMetaMismatch() = view.notifyMetaMismatch()

    fun listRegions() = view.showRegionList(WorldRegionState.get(world).getAllRegions().sortedBy { it.key }, paginator)

    fun listRegionsWithinRadius(radius: Int) {
        val allRegions = WorldRegionState.get(world).getAllRegions()
        val proximityPairs = allRegions.map { Pair(it, it.value.distanceFrom(owner.blockPos)) }
        val withinRadius = proximityPairs.sortedBy { it.second }.filter { it.second <= radius }
        view.showRegionProximities(withinRadius, paginator)
    }

    fun listRegionsContainingSong(songID: String) {
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

    fun listRegionPlaylistSongs(regionName: String) {
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

    fun createRegion(regionName: String, priority: Int) {
        val state = WorldRegionState.get(world)
        val m1 = model.marker1
        val m2 = model.marker2

        when {
            state.regionExists(regionName) -> view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
            m1 != null && m2 != null -> WorldControllers[world].createRegion(regionName, priority, m1, m2, listOf(view))
            else -> view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
        }
    }

    fun showIfRegionsOverlap(firstRegion: String, secondRegion: String) {
        // TODO check if regions overlap
        view.notifyRegionOverlaps("", "", false)
    }

    fun showRegionInfo(regionName: String) {
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

    fun addRegionVolume(regionName: String) {
        val m1 = model.marker1
        val m2 = model.marker2
        if (m1 != null && m2 != null) WorldControllers[world].addRegionVolume(regionName, m1, m2, listOf(view))
        else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun listRegionVolumes(regionName: String) {
        val region = WorldRegionState.get(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionVolumeList(regionName, region.volumes, paginator)
    }

    fun checkRegionContiguous(regionName: String) {
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
    }
}
