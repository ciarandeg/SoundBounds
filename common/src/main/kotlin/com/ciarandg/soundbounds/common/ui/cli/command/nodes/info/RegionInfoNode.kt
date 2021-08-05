package com.ciarandg.soundbounds.common.ui.cli.command.nodes.info

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

object RegionInfoNode : CommandNode(
    LiteralNodeData("region", "display region info", null),
    listOf(
        CommandNode(
            StringArgNodeData(Arguments.regionNameExistingArgument) { ctx, ctrl ->
                ctrl.showRegionInfo(ctx.source.world, Arguments.regionNameExistingArgument.retrieve(ctx))
            },
            listOf(
                CommandNode(
                    LiteralNodeData("playlist", "list songs in region's playlist") { ctx, ctrl ->
                        val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                        ctrl.paginator.setState("sb info region $regionName playlist")
                        ctrl.listRegionPlaylistSongs(ctx.source.world, regionName)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                                ctrl.paginator.setState(
                                    "sb info region $regionName playlist",
                                    Arguments.pageNumArgument.retrieve(ctx)
                                )
                                ctrl.listRegionPlaylistSongs(ctx.source.world, regionName)
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData(
                        "volumes",
                        "list volumes in region"
                    ) { ctx, ctrl ->
                        val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                        ctrl.paginator.setState("sb info region $regionName volumes")
                        ctrl.listRegionVolumes(ctx.source.world, regionName)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                                ctrl.paginator.setState("sb info region $regionName volumes", Arguments.pageNumArgument.retrieve(ctx))
                                ctrl.listRegionVolumes(ctx.source.world, regionName)
                            },
                            listOf()
                        )
                    )
                ),
            )
        )
    )
)
