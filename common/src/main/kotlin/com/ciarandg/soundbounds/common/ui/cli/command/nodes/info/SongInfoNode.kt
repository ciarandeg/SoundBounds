package com.ciarandg.soundbounds.common.ui.cli.command.nodes.info

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

object SongInfoNode : CommandNode(
    LiteralNodeData("song", "display information about a song") { ctx, ctrl ->
        CommandNode(StringArgNodeData(Arguments.songIDExistingArgument) { ctx, ctrl -> }, listOf())
    },
    listOf()
)
