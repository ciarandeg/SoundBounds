package com.ciarandegroot.soundbounds.server.ui.cli

import com.ciarandegroot.soundbounds.common.command.CommandNode
import com.ciarandegroot.soundbounds.common.command.RootNode
import com.ciarandegroot.soundbounds.common.util.Paginator
import com.ciarandegroot.soundbounds.common.util.PlaylistType
import com.ciarandegroot.soundbounds.server.ui.ServerPlayerView
import com.ciarandegroot.soundbounds.server.ui.cli.help.HelpGenerator
import com.ciarandegroot.soundbounds.server.ui.cli.help.HelpTreeNode
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
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

    override fun showNowPlaying() {
        TODO("Not yet implemented")
    }

    override fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos) {
        TODO("Not yet implemented")
    }

    override fun showRegionList(regions: List<String>) {
        TODO("Not yet implemented")
    }

    override fun notifyMetadataSynced() {
        TODO("Not yet implemented")
    }

    override fun notifyRegionCreated(name: String, priority: Int) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionDestroyed(name: String) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionRenamed(from: String, to: String) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showRegionInfo(region: String) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionPlaylistTypeSet(name: String, type: PlaylistType) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun showRegionContiguous(regionName: String) {
        TODO("Not yet implemented")
    }
}
