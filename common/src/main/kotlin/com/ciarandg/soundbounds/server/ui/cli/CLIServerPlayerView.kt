package com.ciarandg.soundbounds.server.ui.cli

import com.ciarandg.soundbounds.common.command.CommandNode
import com.ciarandg.soundbounds.common.regions.Region
import com.ciarandg.soundbounds.common.ui.cli.RootNode
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.cli.help.HelpGenerator
import com.ciarandg.soundbounds.server.ui.cli.help.HelpTreeNode
import com.ciarandg.soundbounds.server.PersistenceUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos

class CLIServerPlayerView(override val owner: PlayerEntity) : PlayerView {
    private val helpTree = HelpTreeNode(RootNode)

    init {
        entityViews[owner] = this
    }

    companion object {
        private val entityViews: HashMap<Entity, CLIServerPlayerView> = HashMap()
        fun getEntityView(e: Entity) = entityViews[e]
    }

    fun showHelp(root: CommandNode = RootNode) {
        owner.sendMessage(
            Paginator.paginate("SoundBounds Help", HelpGenerator.readOut(findHelpNode(root) ?: helpTree)),
            false
        )
    }

    private fun findHelpNode(commandNode: CommandNode, helpTree: HelpTreeNode = this.helpTree): HelpTreeNode? {
        if (commandNode === helpTree.commandNode) return helpTree
        for (c in helpTree.children) {
            val h = findHelpNode(commandNode, c)
            if (h != null) return h
        }
        return null
    }

    override fun showNowPlaying() {} // TODO

    override fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos) = owner.sendMessage(
        LiteralText("Set marker $marker to $pos"), false
    )

    override fun showRegionList(regions: List<Map.Entry<String, Region>>) = owner.sendMessage(
        Paginator.paginate("Region List", regions.mapIndexed { index, entry ->
            val n = index + 1
            val name = entry.key
            val playlistType = entry.value.playlistType.toString().toLowerCase()
            val priority = entry.value.priority
            LiteralText("$n. $name - $playlistType, priority $priority")
        }),
        false
    )

    override fun notifyMetadataSynced() {
        val meta = PersistenceUtils.getServerMetaState().meta
        owner.sendMessage(
            LiteralText("Successfully synced metadata: " +
                    "${meta.composers.size} composers, " +
                    "${meta.groups.size} groups, " +
                    "${meta.songs.size} songs"),
            false
        )
    }

    override fun notifyMetadataSyncFailed() {
        owner.sendMessage(
            LiteralText("Failed to sync metadata"),
            false
        )
    }

    override fun notifyRegionCreated(name: String, priority: Int) {
        owner.sendMessage(
            LiteralText("Created region $name, priority $priority"),
            false
        )
    }
    override fun notifyRegionDestroyed(name: String) = owner.sendMessage(
        LiteralText("Region $name destroyed"), false
    )

    override fun notifyRegionRenamed(from: String, to: String) {
        owner.sendMessage(LiteralText("Region $from renamed to $to"), false)
    }

    override fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) {} // TODO
    override fun showRegionInfo(regionName: String, region: Region) {
        owner.sendMessage(LiteralText(
            "Region $regionName: type ${region.playlistType}, " +
                    "song count ${region.playlist.size}, " +
                    "bounds count ${region.volumes.size}"),
            false)
    }
    override fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int) {
        owner.sendMessage(LiteralText(
            "Region $name priority changed from $oldPriority to $newPriority"
        ), false)
    }

    override fun notifyRegionPlaylistTypeSet(name: String, from: PlaylistType, to: PlaylistType) {
        owner.sendMessage(LiteralText(
            if (from == to) "Region $name is already of type $to!"
            else "Region $name changed from type $from to type $to"
        ), false)
    }

    override fun notifyRegionVolumeAdded(regionName: String, volume: Pair<BlockPos, BlockPos>) =
        owner.sendMessage(LiteralText("Added new volume to $regionName: $volume"), false)

    override fun notifyRegionVolumeRemoved(regionName: String, position: Int, volume: Pair<BlockPos, BlockPos>) =
        owner.sendMessage(
            LiteralText("Removed volume from $regionName at position $position: $volume"),
            false
        )

    override fun showRegionVolumeList(regionName: String, volumes: List<Pair<BlockPos, BlockPos>>) =
        owner.sendMessage(
            Paginator.paginate("Volumes in $regionName", volumes.mapIndexed { i, vol ->
                val pos = i + 1
                LiteralText("$pos.\nCORNER 1: ${vol.first}\nCORNER 2: ${vol.second}" +
                  if (pos < volumes.size) "\n" else ""
                )
            }), false)

    override fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int) =
        owner.sendMessage(
            LiteralText("Added song $song to $regionName at position $pos"),
            false
        )

    override fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int) =
        owner.sendMessage(
            LiteralText("Removed song $song from $regionName at position $pos"),
            false
        )

    override fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int) =
        owner.sendMessage(
            LiteralText("Replaced song $oldSong with $newSong in region $regionName at position $pos"),
            false
        )

    override fun showRegionContiguous(regionName: String) {} // TODO

    override fun notifyFailed(reason: PlayerView.FailureReason) = owner.sendMessage(
        LiteralText(when (reason) {
            PlayerView.FailureReason.POS_MARKERS_MISSING -> "position markers not set"
            PlayerView.FailureReason.NO_SUCH_REGION -> "requested region does not exist"
            PlayerView.FailureReason.REGION_NAME_CONFLICT -> "requested region name is taken"
            PlayerView.FailureReason.VOLUME_INDEX_OOB -> "requested volume index is out of bounds"
            PlayerView.FailureReason.REGION_MUST_HAVE_VOLUME -> "requested region only has one volume"
            PlayerView.FailureReason.NO_METADATA_PRESENT -> "no metadata has been provided to server (try /sb sync-meta)"
            PlayerView.FailureReason.NO_SUCH_SONG -> "requested song ID does not exist"
            PlayerView.FailureReason.SONG_POS_OOB -> "requested song position is out of bounds"
        }), false)
}
