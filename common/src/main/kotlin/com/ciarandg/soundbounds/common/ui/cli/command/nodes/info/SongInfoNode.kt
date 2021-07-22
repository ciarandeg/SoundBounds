package com.ciarandg.soundbounds.common.ui.cli.command.nodes.info

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

object SongInfoNode : CommandNode(
    LiteralNodeData("song", "display information about a song", null),
    listOf(
        CommandNode(
            StringArgNodeData(Arguments.songIDExistingArgument) { ctx, ctrl ->
                ctrl.showSongInfo(Arguments.songIDExistingArgument.retrieve(ctx))
            },
            listOf()
        )
    )
)
