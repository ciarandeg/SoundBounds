package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.persistence.Region
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.ui.PlayerModel
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.PlayerView.FailureReason
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import io.netty.buffer.Unpooled
import me.shedaniel.architectury.networking.NetworkManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

class PlayerController(
    val owner: PlayerEntity,
    private val view: PlayerView = CLIServerPlayerView(owner),
    private val model: PlayerModel = PlayerModel()
) : PlaylistManager(view) {

    fun showNowPlaying() = view.showNowPlaying()
    fun setPosMarker(marker: PosMarker, pos: BlockPos) {
        when (marker) {
            PosMarker.FIRST -> model.marker1 = pos
            PosMarker.SECOND -> model.marker2 = pos
        }
        view.notifyPosMarkerSet(marker, pos)
    }

    fun listRegions(world: ServerWorld, radius: Int = -1) =
        view.showRegionList(Utils.getWorldState(world).getAllRegions().sortedBy { it.key })

    fun syncMetadata(player: ServerPlayerEntity) {
        NetworkManager.sendToPlayer(
            player,
            SoundBounds.SYNC_METADATA_CHANNEL_S2C,
            PacketByteBuf(Unpooled.buffer())
        )
    }

    // this method breaks the MVC pattern a little, but is necessary since syncMetadata is networked
    fun notifyMetadataSynced(successful: Boolean) {
        if (successful) view.notifyMetadataSynced()
        else view.notifyMetadataSyncFailed()
    }

    fun createRegion(world: ServerWorld, regionName: String, priority: Int) {
        val state = Utils.getWorldState(world)
        val m1 = model.marker1
        val m2 = model.marker2

        if (state.regionExists(regionName)) view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
        else if (m1 !=  null && m2 != null) {
            state.putRegion(regionName, Region(priority, volumes = mutableListOf(Pair(m1, m2))))
            Utils.setWorldState(world, state)
            view.notifyRegionCreated(regionName, priority)
        } else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun destroyRegion(world: ServerWorld, regionName: String) {
        val state = Utils.getWorldState(world)
        val removed = state.removeRegion(regionName)
        if (removed == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            Utils.setWorldState(world, state)
            view.notifyRegionDestroyed(regionName)
        }
    }

    fun renameRegion(world: ServerWorld, from: String, to: String) {
        val state = Utils.getWorldState(world)
        val removed = state.removeRegion(from)
        when {
            removed == null -> view.notifyFailed(FailureReason.NO_SUCH_REGION)
            state.regionExists(to) -> view.notifyFailed(FailureReason.REGION_NAME_CONFLICT)
            else -> {
                state.putRegion(to, removed)
                Utils.setWorldState(world, state)
                view.notifyRegionRenamed(from, to)
            }
        }
    }

    fun showIfRegionsOverlap(firstRegion: String, secondRegion: String) {
        // TODO check if regions overlap
        view.notifyRegionOverlaps("", "", false)
    }

    fun showRegionInfo(world: ServerWorld, regionName: String) {
        val region = Utils.getWorldState(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionInfo(regionName, region)
    }

    fun setRegionPriority(world: ServerWorld, regionName: String, priority: Int) {
        val state = Utils.getWorldState(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldPriority = region.priority
            region.priority = priority
            state.putRegion(regionName, region)
            Utils.setWorldState(world, state)
            view.notifyRegionPrioritySet(regionName, oldPriority, priority)
        }
    }

    fun setRegionPlaylistType(world: ServerWorld, regionName: String, type: PlaylistType) {
        val state = Utils.getWorldState(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else {
            val oldType = region.playlistType
            region.playlistType = type
            state.putRegion(regionName, region)
            Utils.setWorldState(world, state)
            view.notifyRegionPlaylistTypeSet(regionName, oldType, type)
        }
    }

    fun addRegionVolume(world: ServerWorld, regionName: String) {
        val state = Utils.getWorldState(world)
        val region = state.getRegion(regionName)
        val m1 = model.marker1
        val m2 = model.marker2

        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else if (m1 != null && m2 != null) {
            val volume = Pair(m1, m2)
            region.volumes.add(volume)
            Utils.setWorldState(world, state)
            view.notifyRegionVolumeAdded(regionName, volume)
        } else view.notifyFailed(FailureReason.POS_MARKERS_MISSING)
    }

    fun removeRegionVolume(world: ServerWorld, regionName: String, index: Int) {
        val state = Utils.getWorldState(world)
        val region = state.getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else if (index < 0 || index >= region.volumes.size) view.notifyFailed(FailureReason.VOLUME_INDEX_OOB)
        else if (region.volumes.size == 1) view.notifyFailed(FailureReason.REGION_MUST_HAVE_VOLUME)
        else {
            val volume = region.volumes.removeAt(index)
            view.notifyRegionVolumeRemoved(regionName, index, volume)
        }
    }

    fun listRegionVolumes(world: ServerWorld, regionName: String) {
        val region = Utils.getWorldState(world).getRegion(regionName)
        if (region == null) view.notifyFailed(FailureReason.NO_SUCH_REGION)
        else view.showRegionVolumeList(regionName, region.volumes)
    }

    fun checkRegionContiguous(world: ServerWorld, regionName: String) {
        // TODO check if region is contiguous
        view.showRegionContiguous(regionName)
    }
}

