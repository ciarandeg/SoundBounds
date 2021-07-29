package com.ciarandg.soundbounds.common.ui.cli.command.nodes

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.BlockPosArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.edit.RegionEditNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.info.InfoNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.list.ListNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.search.SearchNode
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import net.minecraft.util.math.BlockPos

object RootNode : CommandNode(
    LiteralNodeData(
        "sb", null
    ) { _, ctrl ->
        ctrl.paginator.state = PaginatorState("/sb help ${Paginator.PAGE_DELIM}", 1)
        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(ctrl.paginator)
    },
    listOf(
        CommandNode(
            LiteralNodeData("help", "display command info") { _, ctrl ->
                ctrl.paginator.state = PaginatorState("/sb help ${Paginator.PAGE_DELIM}", 1)
                CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(ctrl.paginator)
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                        ctrl.paginator.state =
                            PaginatorState("/sb help ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(ctrl.paginator)
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
                "current-region",
                "show currently active region (switches after fade ends)"
            ) { ctx, ctrl -> ctrl.showCurrentRegion(ctx.source.player) },
            listOf()
        ),
        ListNode,
        InfoNode,
        CommandNode(
            LiteralNodeData(
                "sync-meta",
                "sync client-side metadata to server"
            ) { ctx, ctrl -> ctrl.syncMetadata(ctx.source.player) },
            listOf()
        ),
        CommandNode(
            LiteralNodeData(
                "audit",
                "check regions for missing metadata and empty playlists"
            ) { ctx, ctrl -> ctrl.auditRegions(ctx.source.world) },
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
                    StringArgNodeData(Arguments.regionNameExistingArgument) { ctx, ctrl ->
                        ctrl.destroyRegion(ctx.source.world, Arguments.regionNameExistingArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        RegionEditNode,
        InfoNode,
        SearchNode
    )
)
