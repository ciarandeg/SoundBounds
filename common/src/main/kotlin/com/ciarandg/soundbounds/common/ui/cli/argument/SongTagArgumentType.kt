package com.ciarandg.soundbounds.common.ui.cli.argument

import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import java.util.concurrent.CompletableFuture

class SongTagArgumentType : ArgumentType<String> {
    override fun parse(reader: StringReader?): String = reader?.readString() ?: ""

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(examples, builder)
    }

    override fun getExamples(): Collection<String> = availableTags()

    companion object {
        fun register() = ArgumentTypes.register(
            "song_tag",
            SongTagArgumentType().javaClass,
            ConstantArgumentSerializer(Companion::type)
        )

        fun type(): SongTagArgumentType = SongTagArgumentType()

        private fun availableTags(): Set<String> {
            val meta = ClientMeta.meta ?: return emptySet()
            return meta.songs.entries.flatMap { it.value.tags }.toSortedSet()
        }
    }
}
