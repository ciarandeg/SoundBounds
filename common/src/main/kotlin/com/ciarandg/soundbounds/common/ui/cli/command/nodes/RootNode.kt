package com.ciarandg.soundbounds.common.ui.cli.command.nodes

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.command.SoundBoundsCommand.DEOP_PERM_LEVEL
import com.ciarandg.soundbounds.common.ui.cli.command.SoundBoundsCommand.OP_PERM_LEVEL
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.edit.RegionEditNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.info.InfoNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.list.ListNode
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.search.SearchNode
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView

object RootNode : CommandNode(
    LiteralNodeData(
        "sb", null
    ) { ctx, wctrl, pctrl ->
        if (pctrl != null && ctx.source.hasPermissionLevel(OP_PERM_LEVEL)) {
            pctrl.paginator.setState("sb help")
            CLIServerPlayerView.getEntityView(pctrl.owner)?.showHelp(pctrl.paginator)
        }
    },
    listOf(
        CommandNode(
            LiteralNodeData("help", "display command info") { _, wctrl, pctrl ->
                if (pctrl != null) {
                    pctrl.paginator.setState("sb help")
                    CLIServerPlayerView.getEntityView(pctrl.owner)?.showHelp(pctrl.paginator)
                }
            },
            listOf(
                CommandNode(
                    IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb help", Arguments.pageNumArgument.retrieve(ctx))
                            CLIServerPlayerView.getEntityView(pctrl.owner)?.showHelp(pctrl.paginator)
                        }
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData(
                "now-playing",
                "show currently playing song"
            ) { ctx, wctrl, pctrl -> pctrl?.showNowPlaying(ctx.source.player) },
            listOf(), DEOP_PERM_LEVEL
        ),
        CommandNode(
            LiteralNodeData(
                "current-region",
                "show currently active region (switches after fade ends)"
            ) { ctx, wctrl, pctrl -> pctrl?.showCurrentRegion(ctx.source.player) },
            listOf()
        ),
        ListNode,
        InfoNode,
        CommandNode(
            LiteralNodeData(
                "sync-meta",
                "sync client-side metadata to server"
            ) { ctx, wctrl, pctrl -> pctrl?.syncMetadata(ctx.source.player) },
            listOf()
        ),
        CommandNode(
            LiteralNodeData(
                "audit",
                "check regions for missing metadata and empty playlists"
            ) { _, wctrl, pctrl -> if (pctrl != null) wctrl.auditRegions(listOf(pctrl.view)) },
            listOf()
        ),
        CommandNode(
            LiteralNodeData("visualize", "graphically visualize a region", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.regionNameExistingArgument) { ctx, wctrl, pctrl ->
                        val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                        pctrl?.setVisualizingRegion(regionName)
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
                            IntArgNodeData(Arguments.regionPriorityArgument) { ctx, wctrl, pctrl ->
                                pctrl?.createRegion(
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
                    StringArgNodeData(Arguments.regionNameExistingArgument) { ctx, wctrl, pctrl ->
                        wctrl.destroyRegion(
                            Arguments.regionNameExistingArgument.retrieve(ctx),
                            pctrl?.view?.let { listOf(it) } ?: listOf()
                        )
                    },
                    listOf()
                )
            )
        ),
        RegionEditNode,
        InfoNode,
        SearchNode
    ),
    DEOP_PERM_LEVEL
)
