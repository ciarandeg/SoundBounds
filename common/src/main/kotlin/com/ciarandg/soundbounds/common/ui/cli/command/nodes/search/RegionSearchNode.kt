package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.Paginator
import com.ciarandg.soundbounds.common.ui.cli.PaginatorState
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.sun.org.apache.xpath.internal.Arg

object RegionSearchNode : CommandNode(
    LiteralNodeData("region", "search for regions", null),
    listOf(
        // CommandNode(
        //     LiteralNodeData("current", "search for currently active region", null),
        //     listOf()
        // ),
        CommandNode(
            LiteralNodeData("proximity", "search for regions within proximity of player", null),
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.radiusArgument) { ctx, ctrl ->
                        val radius = Arguments.radiusArgument.retrieve(ctx)
                        ctrl.paginator.setState("sb search region proximity $radius")
                        ctrl.listRegionsWithinRadius(ctx.source.world, radius)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val radius = Arguments.radiusArgument.retrieve(ctx)
                                ctrl.paginator.setState("sb search region proximity $radius", Arguments.pageNumArgument.retrieve(ctx))
                                ctrl.listRegionsWithinRadius(ctx.source.world, radius)
                            },
                            listOf()
                        ),
                    )
                )
            )
        ),
        // CommandNode(
        //     LiteralNodeData("position", "search for regions that contain a particular block", null),
        //     listOf()
        // ),
        CommandNode(
            LiteralNodeData("song", "search for regions that contain a particular song", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.songIDExistingArgument) { ctx, ctrl ->
                        val song = Arguments.songIDExistingArgument.retrieve(ctx)
                        ctrl.paginator.setState("sb search region song $song")
                        ctrl.listRegionsContainingSong(ctx.source.world, song)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val song = Arguments.songIDExistingArgument.retrieve(ctx)
                                ctrl.paginator.setState("sb search region song $song", Arguments.pageNumArgument.retrieve(ctx))
                                ctrl.listRegionsContainingSong(ctx.source.world, song)
                            },
                            listOf()
                        ),
                    )
                )
            )
        )
    )
)
