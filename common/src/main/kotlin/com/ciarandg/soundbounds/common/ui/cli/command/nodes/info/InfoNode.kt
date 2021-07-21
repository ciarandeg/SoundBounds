package com.ciarandg.soundbounds.common.ui.cli.command.nodes.info

import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData

object InfoNode : CommandNode(
    LiteralNodeData("info", "display info about a region, group, composer or song", null),
    listOf(RegionInfoNode, SongInfoNode)
)
