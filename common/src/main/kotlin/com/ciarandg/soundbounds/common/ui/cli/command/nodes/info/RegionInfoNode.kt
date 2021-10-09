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
            StringArgNodeData(Arguments.regionNameExistingArgument) { ctx, wctrl, pctrl ->
                pctrl?.showRegionInfo(Arguments.regionNameExistingArgument.retrieve(ctx))
            },
            listOf(
                CommandNode(
                    LiteralNodeData("playlist", "list songs in region's playlist") { ctx, wctrl, pctrl ->
                        if (pctrl != null) {
                            val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                            pctrl.paginator.setState("sb info region $regionName playlist")
                            pctrl.listRegionPlaylistSongs(regionName)
                        }
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                                if (pctrl != null) {
                                    val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                                    pctrl.paginator.setState(
                                        "sb info region $regionName playlist",
                                        Arguments.pageNumArgument.retrieve(ctx)
                                    )
                                    pctrl.listRegionPlaylistSongs(regionName)
                                }
                            },
                            listOf()
                        )
                    )
                ),
                // CommandNode(
                //     LiteralNodeData(
                //         "volumes",
                //         "list volumes in region"
                //     ) { ctx, wctrl, pctrl ->
                //         if (pctrl != null) {
                //             val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                //             pctrl.paginator.setState("sb info region $regionName volumes")
                //             pctrl.listRegionVolumes(regionName)
                //         }
                //     },
                //     listOf(
                //         CommandNode(
                //             IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                //                 if (pctrl != null) {
                //                     val regionName = Arguments.regionNameExistingArgument.retrieve(ctx)
                //                     pctrl.paginator.setState(
                //                         "sb info region $regionName volumes",
                //                         Arguments.pageNumArgument.retrieve(ctx)
                //                     )
                //                     pctrl.listRegionVolumes(regionName)
                //                 }
                //             },
                //             listOf()
                //         )
                //     )
                // ),
            )
        )
    )
)
