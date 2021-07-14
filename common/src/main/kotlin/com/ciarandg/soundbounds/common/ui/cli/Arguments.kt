package com.ciarandg.soundbounds.common.ui.cli

import com.ciarandg.soundbounds.common.ui.cli.argument.BlockPosArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.IntArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.PlaylistTypeArgumentContainer
import com.ciarandg.soundbounds.common.ui.cli.argument.WordArgumentContainer

object Arguments {
    val pageNumArgument = IntArgumentContainer("page", 1)
    val radiusArgument = IntArgumentContainer("radius", 0)
    val regionPriorityArgument = IntArgumentContainer("priority", 0)
    val songPositionArgument = IntArgumentContainer("song-position", 1)
    val positionArgument = BlockPosArgumentContainer("position")
    val regionArgument = WordArgumentContainer("region")
    val nameArgument = WordArgumentContainer("name")
    val regionNameNewArgument = WordArgumentContainer("new-name")
    val regionNameFirstArgument = WordArgumentContainer("first")
    val regionNameSecondArgument = WordArgumentContainer("second")
    val regionVolumeIndexArgument = IntArgumentContainer("volume-index", 1)
    val songIDArgument = WordArgumentContainer("song-id")
    val newSongIDArgument = WordArgumentContainer("new-song-id")
    val playlistTypeArgument = PlaylistTypeArgumentContainer("playlist-type")
}