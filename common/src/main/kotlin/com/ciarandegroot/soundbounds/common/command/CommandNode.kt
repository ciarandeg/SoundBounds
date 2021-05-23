package com.ciarandegroot.soundbounds.common.command

import com.ciarandegroot.soundbounds.common.command.argument.ArgumentContainer
import com.ciarandegroot.soundbounds.common.util.PlaylistType
import com.ciarandegroot.soundbounds.server.`interface`.ServerPlayerController
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.PosArgument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.BlockPos

open class CommandNode(val data: NodeData, val children: List<CommandNode>)

interface NodeData {
    val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
}

class LiteralNodeData(
    val literal: String,
    val description: String?,
    override val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
) : NodeData

interface ArgNodeData<S, T> : NodeData {
    val arg: ArgumentContainer<S, T>
}

class IntArgNodeData(
    override val arg: ArgumentContainer<Int, Int>,
    override val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
) : ArgNodeData<Int, Int>

class StringArgNodeData(
    override val arg: ArgumentContainer<String, String>,
    override val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
) : ArgNodeData<String, String>

class BlockPosArgNodeData(
    override val arg: ArgumentContainer<PosArgument, BlockPos>,
    override val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
) : ArgNodeData<PosArgument, BlockPos>

class PlaylistTypeArgData(
    override val arg: ArgumentContainer<PlaylistType, PlaylistType>,
    override val work: ((CommandContext<ServerCommandSource>, ServerPlayerController) -> Unit)?
) : ArgNodeData<PlaylistType, PlaylistType>
