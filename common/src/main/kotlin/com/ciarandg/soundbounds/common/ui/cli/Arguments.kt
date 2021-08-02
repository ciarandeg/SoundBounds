package com.ciarandg.soundbounds.common.ui.cli

import com.ciarandg.soundbounds.common.ui.cli.argument.BlockPosArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.BooleanArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.GroupNameArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.IntArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.PlaylistTypeArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.RegionArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.SongIDArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.SongTagArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.WordArgumentContainer

object Arguments {
    val pageNumArgument = IntArgumentContainer("page", 1)
    val radiusArgument = IntArgumentContainer("radius", 0)
    val regionPriorityArgument = IntArgumentContainer("priority", 0)
    val songPositionArgument = IntArgumentContainer("song-position", 1)
    val positionArgument = BlockPosArgumentContainer("position")
    val nameArgument = WordArgumentContainer("name")
    val regionNameExistingArgument = RegionArgumentContainer("region")
    val regionNameExistingFirstArgument = RegionArgumentContainer("first")
    val regionNameExistingSecondArgument = RegionArgumentContainer("second")
    val regionNameNewArgument = WordArgumentContainer("new-name")
    val regionVolumeIndexArgument = IntArgumentContainer("volume-index", 1)
    val songIDExistingArgument = SongIDArgumentContainer("song-id")
    val songTagArgument = SongTagArgumentContainer("tag")
    val playlistTypeArgument = PlaylistTypeArgumentContainer("playlist-type")
    val groupNameArgument = GroupNameArgumentContainer("group-name")
    val queuePersistenceArgument = BooleanArgumentContainer("queue-persist")
}
