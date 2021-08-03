package com.ciarandg.soundbounds.common.ui.cli.argument

import com.ciarandg.soundbounds.common.PlaylistType
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.server.command.ServerCommandSource
import java.security.InvalidParameterException
import java.util.concurrent.CompletableFuture

class PTArgumentType : ArgumentType<PlaylistType> {
    override fun parse(reader: StringReader?): PlaylistType {
        val arg: String = reader?.readString() ?: ""
        for (p in PlaylistType.values()) {
            if (arg == p.name) return p
        }
        throw InvalidParameterException("\"$arg\" is not a Playlist type")
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(examples, builder)
    }

    override fun getExamples(): MutableCollection<String> =
        enumValues<PlaylistType>().map { pl -> pl.name }.toMutableList()

    companion object {
        fun register() = ArgumentTypes.register(
            "playlist_type",
            PTArgumentType().javaClass,
            ConstantArgumentSerializer(Companion::type)
        )

        fun type(): PTArgumentType = PTArgumentType()

        fun getPlaylistType(ctx: CommandContext<ServerCommandSource>, name: String): PlaylistType =
            ctx.getArgument(name, PlaylistType.SEQUENTIAL.javaClass)
    }
}
