package com.ciarandg.soundbounds.common.ui.cli

import com.ciarandg.soundbounds.server.ui.cli.CLIServerPlayerView
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import com.ciarandg.soundbounds.common.command.*
import com.ciarandg.soundbounds.common.command.argument.BlockPosArgumentContainer
import com.ciarandg.soundbounds.common.command.argument.IntArgumentContainer
import com.ciarandg.soundbounds.common.command.argument.PlaylistTypeArgumentContainer
import com.ciarandg.soundbounds.common.command.argument.WordArgumentContainer
import com.ciarandg.soundbounds.common.util.Paginator
import com.ciarandg.soundbounds.common.util.PaginatorState
import com.ciarandg.soundbounds.server.ServerUtils
import com.ciarandg.soundbounds.server.ui.ServerPlayerController
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.server.command.ServerCommandSource as Source

class SoundBoundsCommand {
    companion object {

        fun register(dispatcher: CommandDispatcher<Source>) {
            dispatcher.register(Assembler.assembleLiteral(RootNode))
        }

        private object Assembler {
            fun assembleLiteral(root: CommandNode): LiteralArgumentBuilder<Source> {
                if (root.data !is LiteralNodeData) throw IllegalArgumentException("Must be a literal node")
                val c = CommandManager.literal(root.data.literal)
                assemblyHelper(c, root)
                return c
            }

            fun assembleArg(root: CommandNode): RequiredArgumentBuilder<Source, out Any?> {
                if (root.data !is ArgNodeData<*, *>) throw IllegalArgumentException("Must be an argument node")
                val c = CommandManager.argument(root.data.arg.name, root.data.arg.supply())
                assemblyHelper(c, root)
                return c
            }

            private fun <T : ArgumentBuilder<Source, T>?> assemblyHelper(
                c: ArgumentBuilder<Source, T>,
                root: CommandNode
            ) {
                val work = root.data.work
                if (work != null) c.executes { ctx -> runCommand(ctx, work) }

                for (n in root.children) {
                    if (n.data is LiteralNodeData) c.then(assembleLiteral(n))
                    else if (n.data is ArgNodeData<*, *>) c.then(assembleArg(n))
                }
            }

            private fun runCommand(
                ctx: CommandContext<Source>,
                command: (CommandContext<Source>, ServerPlayerController) -> Unit
            ): Int {
                val source = ctx.source
                val entity = source?.entity
                if (entity is PlayerEntity) {
                    command(ctx,
                        ServerUtils.playerControllers[entity] ?: throw RuntimeException(
                            "Player's controller ought to exist, as it is constructed upon world join"
                        )
                    )
                } else {
                    // TODO add formatting for error message
                    source.sendError(TranslatableText("Invalid command source. Please run in-game as a player"))
                }

                return 1
            }
        }
    }
}

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
                    IntArgNodeData(SBArgs.pageNumArgument) { ctx, ctrl ->
                        Paginator.state =
                            PaginatorState("/sb help ${Paginator.PAGE_DELIM}", SBArgs.pageNumArgument.retrieve(ctx))
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
            ) { _, ctrl -> ctrl.showNowPlaying() },
            listOf()
        ),
        CommandNode(
            LiteralNodeData(
                "list",
                "list all regions in current world"
            ) { ctx, ctrl ->
                Paginator.state = PaginatorState("/sb list ${Paginator.PAGE_DELIM}", 1)
                ctrl.listRegions(ctx.source.world) },
            listOf(
                CommandNode(
                    IntArgNodeData(SBArgs.pageNumArgument) { ctx, ctrl ->
                        Paginator.state =
                            PaginatorState("/sb list ${Paginator.PAGE_DELIM}", SBArgs.pageNumArgument.retrieve(ctx))
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
                    IntArgNodeData(SBArgs.radiusArgument) { ctx, ctrl ->
                        ctrl.listRegions(ctx.source.world, SBArgs.radiusArgument.retrieve(ctx))
                    },
                    listOf(
                        CommandNode(
                            IntArgNodeData(SBArgs.pageNumArgument) { ctx, ctrl ->
                                ctrl.listRegions(
                                    ctx.source.world,
                                    SBArgs.radiusArgument.retrieve(ctx),
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
                    StringArgNodeData(SBArgs.regionArgument) { ctx, ctrl ->
                        ctrl.showRegionInfo(ctx.source.world, SBArgs.regionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData(
                "sync-meta",
                "sync client-side metadata to server"
            ) { _, ctrl -> ctrl.syncMetadata() },
            listOf()
        ),
        CommandNode(
            LiteralNodeData("pos1", "set your first position marker") { ctx, ctrl ->
                ctrl.setPosMarker(PosMarker.FIRST, ctx.source.entity?.blockPos ?: BlockPos(0, 0, 0))
            },
            listOf(
                CommandNode(
                    BlockPosArgNodeData(SBArgs.positionArgument) { ctx, ctrl ->
                        ctrl.setPosMarker(PosMarker.FIRST, SBArgs.positionArgument.retrieve(ctx))
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
                    BlockPosArgNodeData(SBArgs.positionArgument) { ctx, ctrl ->
                        ctrl.setPosMarker(PosMarker.SECOND, SBArgs.positionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("create", "create a region", null),
            listOf(
                CommandNode(
                    StringArgNodeData(SBArgs.nameArgument, null),
                    listOf(
                        CommandNode(
                            IntArgNodeData(SBArgs.regionPriorityArgument) { ctx, ctrl ->
                                ctrl.createRegion(
                                    ctx.source.world,
                                    SBArgs.nameArgument.retrieve(ctx),
                                    SBArgs.regionPriorityArgument.retrieve(ctx)
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
                    StringArgNodeData(SBArgs.regionArgument) { ctx, ctrl ->
                        ctrl.destroyRegion(ctx.source.world, SBArgs.regionArgument.retrieve(ctx))
                    },
                    listOf()
                )
            )
        ),
        CommandNode(
            LiteralNodeData("overlaps", "check if two regions overlap", null),
            listOf(
                CommandNode(
                    StringArgNodeData(SBArgs.regionNameFirstArgument, null),
                    listOf(
                        CommandNode(
                            StringArgNodeData(SBArgs.regionNameSecondArgument) { ctx, ctrl ->
                                ctrl.showIfRegionsOverlap(
                                    SBArgs.regionNameFirstArgument.retrieve(ctx),
                                    SBArgs.regionNameSecondArgument.retrieve(ctx)
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

object RegionEditNode : CommandNode(
        LiteralNodeData("r", "display region-edit command info") { _, ctrl ->
        Paginator.state = PaginatorState("/sb r ${Paginator.PAGE_DELIM}", 1)
        CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(RegionEditNode)
    },
    listOf(
        CommandNode(
            IntArgNodeData(SBArgs.pageNumArgument) { ctx, ctrl ->
                Paginator.state =
                    PaginatorState("/sb r ${Paginator.PAGE_DELIM}", SBArgs.pageNumArgument.retrieve(ctx))
                CLIServerPlayerView.getEntityView(ctrl.owner)?.showHelp(RegionEditNode)
            },
            listOf()
        ),
        CommandNode(
            StringArgNodeData(SBArgs.regionArgument, null),
            listOf(
                CommandNode(
                    LiteralNodeData("rename", "rename region", null),
                    listOf(
                        CommandNode(
                            StringArgNodeData(SBArgs.regionNameNewArgument) { ctx, ctrl ->
                                ctrl.renameRegion(
                                    ctx.source.world,
                                    SBArgs.regionArgument.retrieve(ctx),
                                    SBArgs.regionNameNewArgument.retrieve(ctx)
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
                            IntArgNodeData(SBArgs.regionPriorityArgument) { ctx, ctrl ->
                                ctrl.setRegionPriority(
                                    ctx.source.world,
                                    SBArgs.regionArgument.retrieve(ctx),
                                    SBArgs.regionPriorityArgument.retrieve(ctx)
                                )
                            },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData(
                        "contiguous",
                        "check if region's volumes are contiguous"
                    ) { ctx, ctrl -> ctrl.checkRegionContiguous(SBArgs.regionArgument.retrieve(ctx)) },
                    listOf()
                ),
                CommandNode(
                    LiteralNodeData("v", null, null),
                    listOf(
                        CommandNode(
                            LiteralNodeData("add", "add selected volume to region") { _, _ -> },
                            listOf()
                        ),
                        CommandNode(
                            LiteralNodeData("remove", "remove volume from region") { _, _ -> },
                            listOf()
                        ),
                        CommandNode(
                            LiteralNodeData("list", "list volumes in region") { _, _ -> },
                            listOf()
                        )
                    )
                ),
                CommandNode(
                    LiteralNodeData("p", null, null),
                    listOf(
                        CommandNode(
                            LiteralNodeData("type", "set region's playlist type", null),
                            listOf(
                                CommandNode(
                                    PlaylistTypeArgData(SBArgs.playlistTypeArgument) { ctx, ctrl ->
                                        ctrl.setRegionPlaylistType(
                                            ctx.source.world,
                                            SBArgs.regionArgument.retrieve(ctx),
                                            SBArgs.playlistTypeArgument.retrieve(ctx)
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
                                    StringArgNodeData(SBArgs.songIDArgument) { ctx, ctrl ->
                                        ctrl.appendRegionPlaylistSong(
                                            SBArgs.regionArgument.retrieve(ctx),
                                            SBArgs.songIDArgument.retrieve(ctx)
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
                                    IntArgNodeData(SBArgs.songPositionArgument) { ctx, ctrl ->
                                        ctrl.removeRegionPlaylistSong(
                                            SBArgs.regionArgument.retrieve(ctx),
                                            SBArgs.songPositionArgument.retrieve(ctx)
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
                                    StringArgNodeData(SBArgs.songIDArgument, null),
                                    listOf(
                                        CommandNode(
                                            IntArgNodeData(SBArgs.songPositionArgument) { ctx, ctrl ->
                                                ctrl.insertRegionPlaylistSong(
                                                    SBArgs.regionArgument.retrieve(ctx),
                                                    SBArgs.songIDArgument.retrieve(ctx),
                                                    SBArgs.songPositionArgument.retrieve(ctx)
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
                                    IntArgNodeData(SBArgs.songPositionArgument, null),
                                    listOf(
                                        CommandNode(
                                            StringArgNodeData(SBArgs.newSongIDArgument) { ctx, ctrl ->
                                                ctrl.replaceRegionPlaylistSong(
                                                    SBArgs.regionArgument.retrieve(ctx),
                                                    SBArgs.songPositionArgument.retrieve(ctx),
                                                    SBArgs.newSongIDArgument.retrieve(ctx)
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

object SBArgs {
    val pageNumArgument = IntArgumentContainer("page", 1)
    val radiusArgument = IntArgumentContainer("radius", 0)
    val regionPriorityArgument = IntArgumentContainer("priority", 0)
    val songPositionArgument = IntArgumentContainer("song-position", 1)
    val positionArgument = BlockPosArgumentContainer("position")
    val regionArgument = WordArgumentContainer("region")
    val nameArgument = WordArgumentContainer("name")
    val regionNameNewArgument = WordArgumentContainer("new-name")
    val regionNameFirstArgument = WordArgumentContainer("first")
    val regionNameSecondArgument = WordArgumentContainer("second")
    val songIDArgument = WordArgumentContainer("song-id")
    val newSongIDArgument = WordArgumentContainer("new-song-id")
    val playlistTypeArgument = PlaylistTypeArgumentContainer("playlist-type")
}
