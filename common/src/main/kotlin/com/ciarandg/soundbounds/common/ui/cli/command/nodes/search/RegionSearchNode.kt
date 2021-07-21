package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData

object RegionSearchNode : CommandNode(
    LiteralNodeData("region", "search for regions", null),
    listOf(
        CommandNode(
            LiteralNodeData("current", "search for currently active region", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("proximity", "search for regions within proximity of player", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("position", "search for regions that contain a particular block", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("song", "search for regions that contain a particular song", null),
            listOf()
        )
    )
)
