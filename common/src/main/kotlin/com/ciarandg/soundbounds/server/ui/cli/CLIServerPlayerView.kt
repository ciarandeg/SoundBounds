package com.ciarandg.soundbounds.server.ui.cli

import com.ciarandg.soundbounds.common.command.CommandNode
import com.ciarandg.soundbounds.common.persistence.Region
import com.ciarandg.soundbounds.common.ui.cli.RootNode
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.server.ui.ServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.help.HelpGenerator
import com.ciarandg.soundbounds.server.ui.cli.help.HelpTreeNode
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos

class CLIServerPlayerView(override val owner: PlayerEntity) : ServerPlayerView {
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

    override fun notifyMetadataSynced() {} // TODO
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
                    "bounds count ${region.bounds.size}"),
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

    override fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int) {} // TODO
    override fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int) {} // TODO
    override fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int) {} // TODO
    override fun showRegionContiguous(regionName: String) {} // TODO

    override fun notifyFailed(reason: ServerPlayerView.FailureReason) = owner.sendMessage(
        LiteralText(when (reason) {
            ServerPlayerView.FailureReason.POS_MARKERS_MISSING -> "position markers not set"
            ServerPlayerView.FailureReason.NO_SUCH_REGION -> "requested region does not exist"
            ServerPlayerView.FailureReason.REGION_NAME_CONFLICT -> "requested region name is taken"
        }), false)
}
