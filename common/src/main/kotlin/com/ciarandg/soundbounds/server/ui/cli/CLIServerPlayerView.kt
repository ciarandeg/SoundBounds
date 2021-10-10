package com.ciarandg.soundbounds.server.ui.cli

import com.ciarandg.soundbounds.common.PlaylistType
import com.ciarandg.soundbounds.common.metadata.JsonSongMeta
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.Paginator
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.RootNode
import com.ciarandg.soundbounds.plus
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import com.ciarandg.soundbounds.server.ui.PlayerView
import com.ciarandg.soundbounds.server.ui.cli.Colors.ERROR
import com.ciarandg.soundbounds.server.ui.cli.Colors.ModBadge.formatModBadge
import com.ciarandg.soundbounds.server.ui.cli.Colors.blockPosText
import com.ciarandg.soundbounds.server.ui.cli.Colors.bodyText
import com.ciarandg.soundbounds.server.ui.cli.Colors.listPosText
import com.ciarandg.soundbounds.server.ui.cli.Colors.plainArtistText
import com.ciarandg.soundbounds.server.ui.cli.Colors.playlistTypeText
import com.ciarandg.soundbounds.server.ui.cli.Colors.posMarkerText
import com.ciarandg.soundbounds.server.ui.cli.Colors.priorityText
import com.ciarandg.soundbounds.server.ui.cli.Colors.quantityText
import com.ciarandg.soundbounds.server.ui.cli.Colors.regionNameText
import com.ciarandg.soundbounds.server.ui.cli.Colors.richArtistText
import com.ciarandg.soundbounds.server.ui.cli.Colors.songIDText
import com.ciarandg.soundbounds.server.ui.cli.Colors.songTagListText
import com.ciarandg.soundbounds.server.ui.cli.Colors.songTitleText
import com.ciarandg.soundbounds.server.ui.cli.Colors.volumeText
import com.ciarandg.soundbounds.server.ui.cli.help.HelpGenerator
import com.ciarandg.soundbounds.server.ui.cli.help.HelpTreeNode
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class CLIServerPlayerView(override val owner: PlayerEntity) : PlayerView {
    private val helpTree = HelpTreeNode(RootNode)

    init {
        entityViews[owner] = this
    }

    companion object {
        private val entityViews: HashMap<Entity, CLIServerPlayerView> = HashMap()
        fun getEntityView(e: Entity) = entityViews[e]
        private val modBadge: Text = formatModBadge("SoundBounds")
    }

    fun showHelp(paginator: Paginator, root: CommandNode = RootNode) {
        send(
            paginator.paginate("SoundBounds Help", HelpGenerator.readOut(findHelpNode(root) ?: helpTree)),
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

    override fun showNowPlaying(nowPlaying: String) {
        val meta = ServerMetaState.get().meta
        val songMeta = meta.songs[nowPlaying]
        if (songMeta != null) sendWithBadge(
            bodyText("Now playing: ")
                .append(richArtistText(songMeta.artist, songMeta.featuring))
                .append(bodyText(" - "))
                .append(songTitleText(songMeta.title))
        ) else sendError("Currently playing song does not have server-synced metadata")
    }

    override fun notifyMetaMismatch() = sendError(
        "Client metadata does not match server metadata. " +
            "Update and enable your resource pack, " +
            "or sync your client's metadata to the server."
    )

    override fun showNoSongPlaying() =
        sendWithBadge(bodyText("No song currently playing"))

    override fun showCurrentRegion(regionName: String?) = sendWithBadge(
        if (regionName == null) bodyText("No currently active region")
        else bodyText("Current region: ") + regionNameText(regionName)
    )

    override fun showAuditReport(
        regionsWithEmptyPlaylists: Set<String>,
        regionsMissingMeta: Set<Pair<String, Set<String>>>
    ) {
        val head = bodyText("\nRegions with empty playlists: ")

        if (regionsWithEmptyPlaylists.isNotEmpty()) regionsWithEmptyPlaylists.forEachIndexed { index, regionName ->
            val name = regionNameText(regionName)
            head.append(
                if (index == regionsWithEmptyPlaylists.size - 1) name
                else name.append(bodyText(", "))
            )
        } else head.append(bodyText("NONE"))

        head.append(bodyText("\nRegions containing ghost songs: "))
        if (regionsMissingMeta.isNotEmpty()) regionsMissingMeta.forEach { region ->
            head.append(regionNameText("\n${region.first}")).append(bodyText(": "))
            region.second.forEachIndexed { index, songID ->
                val id = songIDText(songID)
                head.append(
                    if (index == region.second.size - 1) id
                    else id.append(bodyText(", "))
                )
            }
        } else head.append(bodyText("NONE"))
        sendWithBadge(head)
    }

    override fun notifyPosMarkerSet(marker: PosMarker, pos: BlockPos) = sendWithBadge(
        bodyText("Set marker ") + posMarkerText(marker) + bodyText(" to ") + blockPosText(pos)
    )

    override fun notifyVisualizationRegionChanged(regionName: String) = sendWithBadge(
        bodyText("Now visualizing ") + regionNameText(regionName)
    )

    override fun showRegionList(regions: List<Map.Entry<String, RegionData>>, paginator: Paginator) = send(
        paginator.paginate(
            "Region List",
            regions.mapIndexed { index, entry ->
                val n = index + 1
                val name = entry.key
                val playlistType = entry.value.playlistType
                val playlistSize = entry.value.playlist.size
                val priority = entry.value.priority
                listPosText(n) + bodyText(". ") + regionNameText(name) + bodyText(" - ") +
                    bodyText("priority ") + priorityText(priority) +
                    bodyText(", ") + playlistTypeText(playlistType) +
                    bodyText(", ") + quantityText(playlistSize) + bodyText(" songs")
            }
        )
    )

    override fun showRegionProximities(
        regions: List<Pair<Map.Entry<String, RegionData>, Double>>,
        paginator: Paginator
    ) = send(
        paginator.paginate(
            "Nearby Regions",
            regions.mapIndexed { index, entry ->
                val n = index + 1
                val name = entry.first.key
                val priority = entry.first.value.priority
                val distance = entry.second.toInt()
                listPosText(n) + bodyText(". ") + regionNameText(name) + bodyText(" - ") +
                    bodyText("priority ") + priorityText(priority) +
                    bodyText(", $distance ${if (distance == 1) "block" else "blocks"} away")
            }
        )
    )

    override fun notifyMetadataSynced() {
        fun pluralize(size: Int, singular: String) =
            if (size == 1) singular else singular + "s"

        val meta = ServerMetaState.get().meta
        val composers = meta.composers.size
        val groups = meta.groups.size
        val songs = meta.songs.size

        sendWithBadge(
            bodyText("Successfully synced metadata: ") +
                quantityText(composers) + bodyText(" ${pluralize(composers, "composer")}, ") +
                quantityText(groups) + bodyText(" ${pluralize(groups, "group")}, ") +
                quantityText(songs) + bodyText(" ${pluralize(songs, "song")}") +
                bodyText(". Consider auditing your regions (/sb audit) to ensure that your new metadata is compatible.")
        )
    }

    override fun notifyMetadataSyncFailed() {
        sendError("Failed to sync metadata")
    }

    override fun notifyRegionCreated(name: String, priority: Int) {
        sendWithBadge(
            bodyText("Created region ") + regionNameText(name) +
                bodyText(", priority ") + priorityText(priority)
        )
    }
    override fun notifyRegionDestroyed(name: String) = sendWithBadge(
        bodyText("Region ") + regionNameText(name) + bodyText(" destroyed")
    )

    override fun notifyRegionRenamed(from: String, to: String) = sendWithBadge(
        bodyText("Region ") + regionNameText(from) + bodyText(" renamed to ") + regionNameText(to)
    )

    override fun notifyRegionOverlaps(region1: String, region2: String, overlaps: Boolean) {} // TODO
    override fun showRegionInfo(regionName: String, data: RegionData) {
        sendWithBadge(
            bodyText("\nRegion name: ") + regionNameText(regionName) +
                bodyText("\nRegion priority: ") + priorityText(data.priority) +
                bodyText("\nPlaylist type: ") + playlistTypeText(data.playlistType) +
                bodyText("\nSong count: ") + quantityText(data.playlist.size) +
                bodyText("\nVolume count: ") + quantityText(data.bounds.size) +
                bodyText("\nPlaylist queue persistence? ") + bodyText(data.queuePersistence.toString())
        )
    }
    override fun notifyRegionPrioritySet(name: String, oldPriority: Int, newPriority: Int) = sendWithBadge(
        bodyText("Region ") + regionNameText(name) + bodyText(" priority changed from ") +
            priorityText(oldPriority) + bodyText(" to ") + priorityText(newPriority)
    )

    override fun notifyRegionPlaylistTypeSet(name: String, from: PlaylistType, to: PlaylistType) = sendWithBadge(
        if (from == to) bodyText("Region ") + regionNameText(name) +
            bodyText(" is already of type ") + playlistTypeText(to) + bodyText("!")
        else bodyText("Region ") + regionNameText(name) + bodyText(" changed from type ") +
            playlistTypeText(from) + bodyText(" to type ") + playlistTypeText(to)
    )

    override fun notifyRegionVolumeAdded(regionName: String, volume: Pair<BlockPos, BlockPos>) = sendWithBadge(
        bodyText("Added new volume to ") + regionNameText(regionName) + bodyText(": ") + volumeText(volume)
    )

    override fun notifyRegionVolumeRemoved(regionName: String, position: Int, volume: Pair<BlockPos, BlockPos>) =
        sendWithBadge(
            bodyText("Removed volume from ") + regionNameText(regionName) + bodyText(" at position ") +
                listPosText(position) + bodyText(": ") + volumeText(volume)
        )

    override fun showRegionVolumeList(regionName: String, volumes: List<Pair<BlockPos, BlockPos>>, paginator: Paginator) =
        send(
            paginator.paginate(
                "Volumes in $regionName",
                volumes.mapIndexed { i, vol ->
                    val pos = i + 1
                    listPosText(pos) + bodyText(". \nCORNER 1: ") + blockPosText(vol.first) +
                        bodyText("\nCORNER 2: ") + blockPosText(vol.second) +
                        bodyText(if (pos < volumes.size) "\n" else "")
                }
            ),
        )

    override fun notifyRegionPlaylistSongAdded(regionName: String, song: String, pos: Int) =
        sendWithBadge(
            bodyText("Added song ") + songTitleText(song) + bodyText(" to ") +
                regionNameText(regionName) + bodyText(" at position ") + listPosText(pos)
        )

    override fun notifyRegionPlaylistSongRemoved(regionName: String, song: String, pos: Int) =
        sendWithBadge(
            bodyText("Removed song ") + songTitleText(song) + bodyText(" from ") +
                regionNameText(regionName) + bodyText(" at position ") + listPosText(pos)
        )

    override fun notifyRegionPlaylistSongReplaced(regionName: String, oldSong: String, newSong: String, pos: Int) =
        sendWithBadge(
            bodyText("Replaced song ") + songTitleText(oldSong) + bodyText(" with ") +
                songTitleText(newSong) + bodyText(" in region ") + regionNameText(regionName) +
                bodyText(" at position ") + listPosText(pos)
        )

    override fun showRegionContiguous(regionName: String) {} // TODO

    override fun showSongList(songs: List<Pair<String, JsonSongMeta?>>, paginator: Paginator) = send(
        paginator.paginate(
            "Song List",
            songs.mapIndexed { index, song ->
                val n = index + 1
                val id = song.first
                val songMeta = song.second
                val start = listPosText(n).append(bodyText(". ")).append(songIDText(id)).append(bodyText(": "))
                if (songMeta == null) start.append(LiteralText("Missing metadata!").formatted(ERROR))
                else start.append(richArtistText(songMeta.artist, songMeta.featuring))
                    .append(bodyText(" - "))
                    .append(songTitleText(songMeta.title))
            }
        )
    )

    override fun showSongInfo(songID: String, song: JsonSongMeta) = sendWithBadge(
        bodyText("\n")
            .append(richArtistText(song.artist, song.featuring))
            .append(bodyText(" - "))
            .append(songTitleText(song.title))
            .append(bodyText("\nID: "))
            .append(songIDText(songID))
            .append(
                bodyText("\nLooping? ${song.loop}" + "\nHas head? ${song.head != null}" + "\nBody count: ")
            )
            .append(quantityText(song.bodies.size))
            .append(
                if (song.tags.isEmpty()) bodyText("\nNo tags")
                else bodyText("\nTags: ") + songTagListText(song.tags)
            )
    )

    override fun showGroupMembers(groupName: String, members: List<String>) {
        val head = bodyText("Members of ") + plainArtistText(groupName) + bodyText(": ")
        members.forEachIndexed { i, member ->
            head.append(richArtistText(member))
            if (i < members.size - 1) head.append(bodyText(", "))
        }
        sendWithBadge(head)
    }

    override fun notifyPlaylistPersistenceChanged(regionName: String, playlistPersist: Boolean) = sendWithBadge(
        bodyText("Region ") + regionNameText(regionName) + bodyText(" playlist persistence set to $playlistPersist")
    )

    override fun notifyFailed(reason: PlayerView.FailureReason) = sendError(
        when (reason) {
            PlayerView.FailureReason.EMPTY_SELECTION -> "You have not selected any blocks"
            PlayerView.FailureReason.NO_SUCH_REGION -> "Requested region does not exist"
            PlayerView.FailureReason.NO_SUCH_GROUP -> "Requested group does not exist"
            PlayerView.FailureReason.REGION_NAME_CONFLICT -> "Requested region name is taken"
            PlayerView.FailureReason.VOLUME_INDEX_OOB -> "Requested volume index is out of bounds"
            PlayerView.FailureReason.REGION_MUST_HAVE_VOLUME -> "Requested region only has one volume"
            PlayerView.FailureReason.NO_METADATA_PRESENT -> "No metadata has been provided to server (try /sb sync-meta)"
            PlayerView.FailureReason.NO_SUCH_SONG -> "Requested song ID does not exist"
            PlayerView.FailureReason.GHOST_SONG -> "Although there are regions containing requested song ID, the song has no metadata. Please delete it or resync."
            PlayerView.FailureReason.SONG_POS_OOB -> "Requested song position is out of bounds"
            PlayerView.FailureReason.PLAYER_NOT_SYNCER -> "You are not whitelisted to sync metadata"
        }
    )

    private fun sendWithBadge(msg: MutableText) =
        send(modBadge.shallowCopy() + LiteralText(" ") + msg)
    private fun sendError(msg: String) = sendError(LiteralText(msg))
    private fun sendError(msg: MutableText) =
        send(modBadge.shallowCopy() + LiteralText(" ") + msg.formatted(Colors.ERROR))
    private fun send(msg: Text) = owner.sendMessage(msg, false)
}
