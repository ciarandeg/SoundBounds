package com.ciarandg.soundbounds.common.ui.cli.command.nodes.search

import com.ciarandg.soundbounds.common.ui.cli.Arguments
import com.ciarandg.soundbounds.common.ui.cli.CommandNode
import com.ciarandg.soundbounds.common.ui.cli.IntArgNodeData
import com.ciarandg.soundbounds.common.ui.cli.LiteralNodeData
import com.ciarandg.soundbounds.common.ui.cli.StringArgNodeData
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState

object SongSearchNode : CommandNode(
    LiteralNodeData("song", "search for songs", null),
    listOf(
        CommandNode(
            LiteralNodeData("artist", "search for songs by (or featuring) a particular artist", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("title", "search for songs by title", null),
            listOf()
        ),
        CommandNode(
            LiteralNodeData("tag", "search for songs with a particular tag", null),
            listOf(
                CommandNode(
                    StringArgNodeData(Arguments.songTagArgument) { ctx, ctrl ->
                        val tag = Arguments.songTagArgument.retrieve(ctx)
                        ctrl.paginator.state =
                            PaginatorState("/sb search song tag $tag ${Paginator.PAGE_DELIM}", 1)
                        ctrl.listSongsContainingTag(tag)
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(Arguments.pageNumArgument) { ctx, ctrl ->
                                val tag = Arguments.songTagArgument.retrieve(ctx)
                                ctrl.paginator.state = PaginatorState(
                                    "/sb search song tag $tag ${Paginator.PAGE_DELIM}",
                                    Arguments.pageNumArgument.retrieve(ctx)
                                )
                                ctrl.listSongsContainingTag(tag)
                            },
                            listOf()
                        ),
                    )
                )
            )
        )
    )
)
