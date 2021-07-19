package com.ciarandg.soundbounds.common.ui.cli.nodes

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.BlockPosArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import net.minecraft.util.math.BlockPos

object RootNode : CommandNode(
    LiteralNodeData(
        "sb", null
    ) { _, ctrl ->
        Paginator.state = PaginatorState("/sb help ${Paginator.PAGE_DELIM}", 1)
        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp()
    },
    listOf(
        CommandNode(
            LiteralNodeData("help", "display command info") { _, ctrl ->
                Paginator.state = PaginatorState("/sb help ${Paginator.PAGE_DELIM}", 1)
                CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp()
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                        Paginator.state =
                            PaginatorState("/sb help ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp()
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData(
                "now-playing",
                "show currently playing song"
            ) { ctx, ctrl -> ctrl.showNowPlaying(ctx.source.player) },
            listOf()
        ),
        CommandNode(
            LiteralNodeData(
                "list",
                "list all regions in current world"
            ) { ctx, ctrl ->
                Paginator.state = PaginatorState("/sb list ${Paginator.PAGE_DELIM}", 1)
                ctrl.listRegions(ctx.source.world)
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                        Paginator.state =
                            PaginatorState("/sb list ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                        ctrl.listRegions(ctx.source.world)
                    },
                    listOf()
                ),
            )
        ),
        CommandNode(
            LiteralNodeData("nearby", "list all regions within radius", null),
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.radiusArgument) { ctx, ctrl ->
                        ctrl.listRegions(ctx.source.world, Arguments.radiusArgument.retrieve(ctx))
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                ctrl.listRegions(
                                    ctx.source.world,
                                    Arguments.radiusArgument.retrieve(ctx),
                                )
                            },
                            listOf()
                        )
                    )
                )
            )
        ),
        CommandNode(
            LiteralNodeData("info", "display a region's properties", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.regionArgument) { ctx, ctrl ->
                        ctrl.showRegionInfo(ctx.source.world, Arguments.regionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData(
                "sync-meta",
                "sync client-side metadata to server"
            ) { ctx, ctrl -> ctrl.syncMetadata(ctx.source.player) },
            listOf()
        ),
        CommandNode(
            LiteralNodeData("pos1", "set your first position marker") { ctx, ctrl ->
                ctrl.setPosMarker(PosMarker.FIRST, ctx.source.entity?.blockPos ?: BlockPos(0, 0, 0))
            },
            listOf(
                CommandNode(
                    BlockPosArgNodeData(Arguments.positionArgument) { ctx, ctrl ->
                        ctrl.setPosMarker(PosMarker.FIRST, Arguments.positionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("pos2", "set your second position marker") { ctx, ctrl ->
                ctrl.setPosMarker(PosMarker.SECOND, ctx.source.entity?.blockPos ?: BlockPos(0, 0, 0))
            },
            listOf(
                CommandNode(
                    BlockPosArgNodeData(Arguments.positionArgument) { ctx, ctrl ->
                        ctrl.setPosMarker(PosMarker.SECOND, Arguments.positionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("create", "create a region", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.nameArgument, null),
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.regionPriorityArgument) { ctx, ctrl ->
                                ctrl.createRegion(
                                    ctx.source.world,
                                    Arguments.nameArgument.retrieve(ctx),
                                    Arguments.regionPriorityArgument.retrieve(ctx)
                                )
                            },
                            listOf()
                        )
                    )
                )
            )
        ),
        CommandNode(
            LiteralNodeData("destroy", "destroy a region", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.regionArgument) { ctx, ctrl ->
                        ctrl.destroyRegion(ctx.source.world, Arguments.regionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("overlaps", "check if two regions overlap", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.regionNameFirstArgument, null),
                    listOf(
                        CommandNode(
                            StringArgNodeData(Arguments.regionNameSecondArgument) { ctx, ctrl ->
                                ctrl.showIfRegionsOverlap(
                                    Arguments.regionNameFirstArgument.retrieve(ctx),
                                    Arguments.regionNameSecondArgument.retrieve(ctx)
                                )
                            },
                            listOf()
                        )
                    )
                )
            )
        ),
        RegionEditNode
    )
)
