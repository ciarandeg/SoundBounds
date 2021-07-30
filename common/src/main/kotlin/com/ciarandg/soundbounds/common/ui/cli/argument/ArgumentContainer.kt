package com.ciarandg.soundbounds.common.ui.cli.argument

import com.ciarandg.soundbounds.common.util.PlaylistType
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.math.BlockPos

open class ArgumentContainer<S, T>(
    val name: String,
    val supply: () -> ArgumentType<S>,
    private val namedRetrieve: (CommandContext<ServerCommandSource>, String) -> T
) {
    val retrieve: (CommandContext<ServerCommandSource>) -> T = { ctx -> namedRetrieve(ctx, name) }
}

class IntArgumentContainer(name: String, min: Int = 1) :
    ArgumentContainer<Int, Int>(
        name,
        { IntegerArgumentType.integer(min) },
        IntegerArgumentType::getInteger
    )

class WordArgumentContainer(name: String) :
    ArgumentContainer<String, String>(
        name,
        StringArgumentType::word,
        StringArgumentType::getString
    )

class BlockPosArgumentContainer(name: String) :
    ArgumentContainer<PosArgument, BlockPos>(
        name,
        BlockPosArgumentType::blockPos,
        BlockPosArgumentType::getBlockPos
    )

class RegionArgumentContainer(name: String) :
    ArgumentContainer<String, String>(
        name,
        RegionArgumentType::type,
        StringArgumentType::getString
    )

class SongIDArgumentContainer(name: String) :
    ArgumentContainer<String, String>(
        name,
        SongIDArgumentType::type,
        StringArgumentType::getString
    )

class SongTagArgumentContainer(name: String) :
    ArgumentContainer<String, String>(
        name,
        SongTagArgumentType::type,
        StringArgumentType::getString
    )

class GroupNameArgumentContainer(name: String) :
    ArgumentContainer<String, String>(
        name,
        GroupNameArgumentType::type,
        StringArgumentType::getString
    )

class PlaylistTypeArgumentContainer(name: String) :
    ArgumentContainer<PlaylistType, PlaylistType>(
        name,
        PTArgumentType::type,
        PTArgumentType::getPlaylistType
    )
