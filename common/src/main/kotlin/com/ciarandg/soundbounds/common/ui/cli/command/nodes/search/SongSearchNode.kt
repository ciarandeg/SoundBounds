package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData

object SongSearchNode : CommandNode(
    LiteralNodeData("song", "search for songs", null),
    listOf(
        // CommandNode(
        //     LiteralNodeData("artist", "search for songs by (or featuring) a particular artist", null),
        //     listOf()
        // ),
        // CommandNode(
        //     LiteralNodeData("title", "search for songs by title", null),
        //     listOf()
        // ),
        CommandNode(
            LiteralNodeData("tag", "search for songs with a particular tag", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.songTagArgument) { ctx, wctrl, pctrl ->
                        val tag = Arguments.songTagArgument.retrieve(ctx)
                        if (pctrl != null) {
                            pctrl.paginator.setState("sb search song tag $tag")
                            pctrl.listSongsContainingTag(tag)
                        }
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, wctrl, pctrl ->
                                val tag = Arguments.songTagArgument.retrieve(ctx)
                                if (pctrl != null) {
                                    pctrl.paginator.setState("sb search song tag $tag", Arguments.pageNumArgument.retrieve(ctx))
                                    pctrl.listSongsContainingTag(tag)
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
