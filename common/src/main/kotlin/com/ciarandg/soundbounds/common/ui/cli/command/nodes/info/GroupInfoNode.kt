package com.ciarandg.soundbounds.common.ui.cli.command.nodes.info

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

object GroupInfoNode : CommandNode(
    LiteralNodeData("group", "display information about a group", null),
    listOf(
        CommandNode(
            StringArgNodeData(Arguments.groupNameArgument) { ctx, ctrl ->
                ctrl.showGroupInfo(Arguments.groupNameArgument.retrieve(ctx))
            },
            listOf()
        )
    )
)
