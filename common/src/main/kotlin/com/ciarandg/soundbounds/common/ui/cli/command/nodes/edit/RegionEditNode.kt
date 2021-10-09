package com.ciarandg.soundbounds.common.ui.cli.command.nodes.edit

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.BoolArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.PlaylistTypeArgData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView

object RegionEditNode : CommandNode(
    LiteralNodeData("edit", "display region edit command info") { _, wctrl, pctrl ->
        if (pctrl != null) {
            pctrl.paginator.setState("sb edit")
            CLIServerPlayerView.getEntityView(pctrl.owner)?.showHelp(pctrl.paginator, RegionEditNode)
        }
    },
    listOf(
        CommandNode(
            IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                if (pctrl != null) {
                    pctrl.paginator.setState("sb edit", Arguments.pageNumArgument.retrieve(ctx))
                    CLIServerPlayerView.getEntityView(pctrl.owner)?.showHelp(pctrl.paginator, RegionEditNode)
                }
            },
            listOf()
        ),
        CommandNode(
            StringArgNodeData(Arguments.regionNameExistingArgument, null),
            listOf(
                CommandNode(
                    LiteralNodeData("rename", "rename region", null),
                    listOf(
                        CommandNode(
                            StringArgNodeData(Arguments.regionNameNewArgument) { ctx, wctrl, pctrl ->
                                wctrl.renameRegion(
                                    Arguments.regionNameExistingArgument.retrieve(ctx),
                                    Arguments.regionNameNewArgument.retrieve(ctx),
                                    pctrl?.view?.let { listOf(it) } ?: listOf()
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
                            IntArgNodeData(Arguments.regionPriorityArgument) { ctx, wctrl, pctrl ->
                                wctrl.setRegionPriority(
                                    Arguments.regionNameExistingArgument.retrieve(ctx),
                                    Arguments.regionPriorityArgument.retrieve(ctx),
                                    pctrl?.view?.let { listOf(it) } ?: listOf()
                                )
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData("volumes", null, null),
                    listOf(
                        // CommandNode(
                        //     LiteralNodeData(
                        //         "add",
                        //         "add selected volume to region"
                        //     ) { ctx, wctrl, pctrl ->
                        //         pctrl?.addRegionVolume(Arguments.regionNameExistingArgument.retrieve(ctx))
                        //     },
                        //     listOf()
                        // ),
                        // CommandNode(
                        //     LiteralNodeData("remove", "remove volume from region", null),
                        //     listOf(
                        //         CommandNode(
                        //             IntArgNodeData(Arguments.regionVolumeIndexArgument) { ctx, wctrl, pctrl ->
                        //                 wctrl.removeRegionVolume(
                        //                     Arguments.regionNameExistingArgument.retrieve(ctx),
                        //                     Arguments.regionVolumeIndexArgument.retrieve(ctx) - 1,
                        //                     pctrl?.view?.let { listOf(it) } ?: listOf()
                        //                 )
                        //             },
                        //             listOf()
                        //         )
                        //     )
                        // )
                    )
                ),
                CommandNode(
                    LiteralNodeData("playlist", null, null),
                    listOf(
                        CommandNode(
                            LiteralNodeData("type", "set region's playlist type", null),
                            listOf(
                                CommandNode(
                                    PlaylistTypeArgData(Arguments.playlistTypeArgument) { ctx, wctrl, pctrl ->
                                        wctrl.setRegionPlaylistType(
                                            Arguments.regionNameExistingArgument.retrieve(ctx),
                                            Arguments.playlistTypeArgument.retrieve(ctx),
                                            pctrl?.view?.let { listOf(it) } ?: listOf()
                                        )
                                    },
                                    listOf()
                                )
                            )
                        ),
                        CommandNode(
                            LiteralNodeData(
                                "retain-queue",
                                "playlist will proceed to next song on region re-entry if true",
                                null
                            ),
                            listOf(
                                CommandNode(
                                    BoolArgNodeData(Arguments.retainQueueArgument) { ctx, wctrl, pctrl ->
                                        wctrl.setRegionPlaylistQueuePersistence(
                                            Arguments.regionNameExistingArgument.retrieve(ctx),
                                            Arguments.retainQueueArgument.retrieve(ctx),
                                            pctrl?.view?.let { listOf(it) } ?: listOf()
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
                                    StringArgNodeData(Arguments.songIDExistingArgument) { ctx, wctrl, pctrl ->
                                        wctrl.appendRegionPlaylistSong(
                                            Arguments.regionNameExistingArgument.retrieve(ctx),
                                            Arguments.songIDExistingArgument.retrieve(ctx),
                                            pctrl?.view?.let { listOf(it) } ?: listOf()
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
                                    IntArgNodeData(Arguments.songPositionArgument) { ctx, wctrl, pctrl ->
                                        wctrl.removeRegionPlaylistSong(
                                            Arguments.regionNameExistingArgument.retrieve(ctx),
                                            Arguments.songPositionArgument.retrieve(ctx),
                                            pctrl?.view?.let { listOf(it) } ?: listOf()
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
                                    StringArgNodeData(Arguments.songIDExistingArgument, null),
                                    listOf(
                                        CommandNode(
                                            IntArgNodeData(Arguments.songPositionArgument) { ctx, wctrl, pctrl ->
                                                wctrl.insertRegionPlaylistSong(
                                                    Arguments.regionNameExistingArgument.retrieve(ctx),
                                                    Arguments.songIDExistingArgument.retrieve(ctx),
                                                    Arguments.songPositionArgument.retrieve(ctx),
                                                    pctrl?.view?.let { listOf(it) } ?: listOf()
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
                                            StringArgNodeData(Arguments.songIDExistingArgument) { ctx, wctrl, pctrl ->
                                                wctrl.replaceRegionPlaylistSong(
                                                    Arguments.regionNameExistingArgument.retrieve(ctx),
                                                    Arguments.songPositionArgument.retrieve(ctx),
                                                    Arguments.songIDExistingArgument.retrieve(ctx),
                                                    pctrl?.view?.let { listOf(it) } ?: listOf()
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
