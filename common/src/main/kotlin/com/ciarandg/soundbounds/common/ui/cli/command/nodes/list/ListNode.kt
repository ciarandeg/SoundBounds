package com.ciarandg.soundbounds.common.ui.cli.command.nodes.list

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData

object ListNode : CommandNode(
    LiteralNodeData("list", null, null),
    listOf(
        CommandNode(
            LiteralNodeData("regions", "list all regions in current world") { ctx, wctrl, pctrl ->
                if (pctrl != null) {
                    pctrl.paginator.setState("sb list regions")
                    pctrl.listRegions()
                }
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb list regions", Arguments.pageNumArgument.retrieve(ctx))
                            pctrl.listRegions()
                        }
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("songs", "list all songs") { ctx, wctrl, pctrl ->
                if (pctrl != null) {
                    pctrl.paginator.setState("sb list songs")
                    pctrl.listSongs()
                }
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb list songs", Arguments.pageNumArgument.retrieve(ctx))
                            pctrl.listSongs()
                        }
                    },
                    listOf()
                )
            )
        )
    )
)
