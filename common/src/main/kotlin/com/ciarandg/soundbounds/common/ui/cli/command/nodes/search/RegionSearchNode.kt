package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

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
                    IntArgNodeData(Arguments.radiusArgument) { ctx, wctrl, pctrl ->
                        val radius = Arguments.radiusArgument.retrieve(ctx)
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb search region proximity $radius")
                            pctrl.listRegionsWithinRadius(radius)
                        }
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                                val radius = Arguments.radiusArgument.retrieve(ctx)
                                if (pctrl != null) {
                                    pctrl.paginator.setState("sb search region proximity $radius", Arguments.pageNumArgument.retrieve(ctx))
                                    pctrl.listRegionsWithinRadius(radius)
                                }
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
                    StringArgNodeData(Arguments.songIDExistingArgument) { ctx, wctrl, pctrl ->
                        val song = Arguments.songIDExistingArgument.retrieve(ctx)
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb search region song $song")
                            pctrl.listRegionsContainingSong(song)
                        }
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                                val song = Arguments.songIDExistingArgument.retrieve(ctx)
                                if (pctrl != null) {
                                    pctrl.paginator.setState("sb search region song $song", Arguments.pageNumArgument.retrieve(ctx))
                                    pctrl.listRegionsContainingSong(song)
                                }
                            },
                            listOf()
                        ),
                    )
                )
            )
        )
    )
)
