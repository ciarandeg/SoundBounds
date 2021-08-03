package com.ciarandg.soundbounds.common.ui.cli.command.nodes.list

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.Paginator
import com.ciarandg.soundbounds.common.ui.cli.PaginatorState

object ListNode : CommandNode(
    LiteralNodeData("list", null, null),
    listOf(
        CommandNode(
            LiteralNodeData("regions", "list all regions in current world") { ctx, ctrl ->
                ctrl.paginator.state = PaginatorState("/sb list regions ${Paginator.PAGE_DELIM}")
                ctrl.listRegions(ctx.source.world)
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                        ctrl.paginator.state =
                            PaginatorState("/sb list regions ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                        ctrl.listRegions(ctx.source.world)
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("songs", "list all songs") { ctx, ctrl ->
                ctrl.paginator.state = PaginatorState("/sb list songs ${Paginator.PAGE_DELIM}")
                ctrl.listSongs()
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                        ctrl.paginator.state =
                            PaginatorState("/sb list songs ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                        ctrl.listSongs()
                    },
                    listOf()
                )
            )
        )
    )
)
