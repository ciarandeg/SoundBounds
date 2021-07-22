package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState

object RegionSearchNode : CommandNode(
    LiteralNodeData("region", "search for regions", null),
    listOf(
        CommandNode(
            LiteralNodeData("current", "search for currently active region", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("proximity", "search for regions within proximity of player", null),
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.radiusArgument) { ctx, ctrl ->
                        val radius = Arguments.radiusArgument.retrieve(ctx)
                        ctrl.paginator.state = PaginatorState("/sb search region proximity $radius ${Paginator.PAGE_DELIM}", 1)
                        ctrl.listRegionsWithinRadius(ctx.source.world, radius)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val radius = Arguments.radiusArgument.retrieve(ctx)
                                ctrl.paginator.state = PaginatorState(
                                    "/sb search region proximity $radius ${Paginator.PAGE_DELIM}",
                                    Arguments.pageNumArgument.retrieve(ctx)
                                )
                                ctrl.listRegionsWithinRadius(ctx.source.world, radius)
                            },
                            listOf()
                        ),
                    )
                )
            )
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
