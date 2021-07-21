package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData

object SongSearchNode : CommandNode(
    LiteralNodeData("song", "search for songs", null),
    listOf(
        CommandNode(
            LiteralNodeData("artist", "search for songs by (or featuring) a particular artist", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("title", "search for songs by title", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("tag", "search for songs with a particular tag", null),
            listOf()
        )
    )
)
