package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData

object SearchNode : CommandNode(
    LiteralNodeData(
        "search",
        "search for regions and songs that match particular properties",
        null
    ),
    listOf(RegionSearchNode, SongSearchNode)
)
