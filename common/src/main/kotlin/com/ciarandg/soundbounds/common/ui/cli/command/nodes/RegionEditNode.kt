package com.ciarandg.soundbounds.common.ui.cli.command.nodes

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.PlaylistTypeArgData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView

object RegionEditNode : CommandNode(
    LiteralNodeData("edit", "display region edit command info") { _, ctrl ->
        Paginator.state = PaginatorState("/sb edit ${Paginator.PAGE_DELIM}", 1)
        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(RegionEditNode)
    },
    listOf(
        CommandNode(
            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                Paginator.state =
                    PaginatorState("/sb edit ${Paginator.PAGE_DELIM}", Arguments.pageNumArgument.retrieve(ctx))
                CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(RegionEditNode)
            },
            listOf()
        ),
        CommandNode(
            StringArgNodeData(Arguments.regionArgument, null),
            listOf(
                CommandNode(
                    LiteralNodeData("rename", "rename region", null),
                    listOf(
                        CommandNode(
                            StringArgNodeData(Arguments.regionNameNewArgument) { ctx, ctrl ->
                                ctrl.renameRegion(
                                    ctx.source.world,
                                    Arguments.regionArgument.retrieve(ctx),
                                    Arguments.regionNameNewArgument.retrieve(ctx)
                                )
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData(
                        "priority",
                        "set region's priority (min 0)",
                        null
                    ),
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.regionPriorityArgument) { ctx, ctrl ->
                                ctrl.setRegionPriority(
                                    ctx.source.world,
                                    Arguments.regionArgument.retrieve(ctx),
                                    Arguments.regionPriorityArgument.retrieve(ctx)
                                )
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData("volumes", null, null),
                    listOf(
                        CommandNode(
                            LiteralNodeData(
                                "add",
                                "add selected volume to region"
                            ) { ctx, ctrl ->
                                ctrl.addRegionVolume(ctx.source.world, Arguments.regionArgument.retrieve(ctx))
                            },
                            listOf()
                        ),
                        CommandNode(
                            LiteralNodeData("remove", "remove volume from region", null),
                            listOf(
                                CommandNode(
                                    IntArgNodeData(Arguments.regionVolumeIndexArgument) { ctx, ctrl ->
                                        ctrl.removeRegionVolume(
                                            ctx.source.world,
                                            Arguments.regionArgument.retrieve(ctx),
                                            Arguments.regionVolumeIndexArgument.retrieve(ctx) - 1
                                        )
                                    },
                                    listOf()
                                )
                            )
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData("playlist", null, null),
                    listOf(
                        CommandNode(
                            LiteralNodeData("type", "set region's playlist type", null),
                            listOf(
                                CommandNode(
                                    PlaylistTypeArgData(Arguments.playlistTypeArgument) { ctx, ctrl ->
                                        ctrl.setRegionPlaylistType(
                                            ctx.source.world,
                                            Arguments.regionArgument.retrieve(ctx),
                                            Arguments.playlistTypeArgument.retrieve(ctx)
                                        )
                                    },
                                    listOf()
                                )
                            )
                        ),
                        CommandNode(
                            LiteralNodeData("append", "append new song to region's playlist", null),
                            listOf(
                                CommandNode(
                                    StringArgNodeData(Arguments.songIDArgument) { ctx, ctrl ->
                                        ctrl.appendRegionPlaylistSong(
                                            ctx.source.world,
                                            Arguments.regionArgument.retrieve(ctx),
                                            Arguments.songIDArgument.retrieve(ctx)
                                        )
                                    },
                                    listOf()
                                )
                            )
                        ),
                        CommandNode(
                            LiteralNodeData("remove", "remove song from region's playlist", null),
                            listOf(
                                CommandNode(
                                    IntArgNodeData(Arguments.songPositionArgument) { ctx, ctrl ->
                                        ctrl.removeRegionPlaylistSong(
                                            ctx.source.world,
                                            Arguments.regionArgument.retrieve(ctx),
                                            Arguments.songPositionArgument.retrieve(ctx)
                                        )
                                    },
                                    listOf()
                                )
                            )
                        ),
                        CommandNode(
                            LiteralNodeData("insert", "insert song into region's playlist", null),
                            listOf(
                                CommandNode(
                                    StringArgNodeData(Arguments.songIDArgument, null),
                                    listOf(
                                        CommandNode(
                                            IntArgNodeData(Arguments.songPositionArgument) { ctx, ctrl ->
                                                ctrl.insertRegionPlaylistSong(
                                                    ctx.source.world,
                                                    Arguments.regionArgument.retrieve(ctx),
                                                    Arguments.songIDArgument.retrieve(ctx),
                                                    Arguments.songPositionArgument.retrieve(ctx)
                                                )
                                            },
                                            listOf()
                                        )
                                    )
                                )
                            )
                        ),
                        CommandNode(
                            LiteralNodeData("replace", "replace song in region's playlist", null),
                            listOf(
                                CommandNode(
                                    IntArgNodeData(Arguments.songPositionArgument, null),
                                    listOf(
                                        CommandNode(
                                            StringArgNodeData(Arguments.newSongIDArgument) { ctx, ctrl ->
                                                ctrl.replaceRegionPlaylistSong(
                                                    ctx.source.world,
                                                    Arguments.regionArgument.retrieve(ctx),
                                                    Arguments.songPositionArgument.retrieve(ctx),
                                                    Arguments.newSongIDArgument.retrieve(ctx)
                                                )
                                            },
                                            listOf()
                                        )
                                    )
                                )
                            )
                        ),
                    )
                )
            )
        )
    )
)
