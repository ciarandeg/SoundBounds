package com.ciarandg.soundbounds.common.ui.cli.command.nodes

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState

object InfoNode : CommandNode(
    LiteralNodeData("info", "display info about a region, group, composer or song", null),
    listOf(RegionInfoNode, SongInfoNode)
)

private object RegionInfoNode : CommandNode(
    LiteralNodeData("region", "display region info", null),
    listOf(
        CommandNode(
            StringArgNodeData(Arguments.regionArgument) { ctx, ctrl ->
                ctrl.showRegionInfo(ctx.source.world, Arguments.regionArgument.retrieve(ctx))
            },
            listOf(
                CommandNode(
                    LiteralNodeData("playlist", "list songs in region's playlist") { ctx, ctrl -> },
                    listOf()
                ),
                CommandNode(
                    LiteralNodeData(
                        "volumes",
                        "list volumes in region"
                    ) { ctx, ctrl ->
                        val regionName = Arguments.regionArgument.retrieve(ctx)
                        Paginator.state = PaginatorState(
                            "/sb info region $regionName volumes ${Paginator.PAGE_DELIM}", 1
                        )
                        ctrl.listRegionVolumes(ctx.source.world, regionName)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val regionName = Arguments.regionArgument.retrieve(ctx)
                                Paginator.state = PaginatorState(
                                    "/sb info region $regionName volumes ${Paginator.PAGE_DELIM}",
                                    Arguments.pageNumArgument.retrieve(ctx)
                                )
                                ctrl.listRegionVolumes(ctx.source.world, regionName)
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData(
                        "contiguous",
                        "check if region's volumes are contiguous"
                    ) { ctx, ctrl ->
                        ctrl.checkRegionContiguous(ctx.source.world, Arguments.regionArgument.retrieve(ctx))
                    },
                    listOf()
                ),
            )
        )
    )
)

private object SongInfoNode : CommandNode(
    LiteralNodeData("song", "display information about a song") { ctx, ctrl ->
        CommandNode(StringArgNodeData(Arguments.songIDExistingArgument) { ctx, ctrl -> }, listOf())
    },
    listOf()
)

private object GroupInfoNode : CommandNode(
    LiteralNodeData("group", null) { ctx, ctrl -> },
    listOf(
        // CommandNode(StringArgNodeData(Arguments.groupNameArgument) { ctx, ctrl -> }, listOf())
    )
)

private object ComposerInfoNode : CommandNode(
    LiteralNodeData("composer", null) { ctx, ctrl ->
        // CommandNode(StringArgNodeData(Arguments.composerNameArgument) { ctx, ctrl -> }, listOf())
    },
    listOf()
)
