package com.ciarandg.soundbounds.common.ui.cli.argument

import com.ciarandg.soundbounds.client.regions.ClientWorldRegions
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import java.util.concurrent.CompletableFuture

class RegionArgumentType : ArgumentType<String> {
    override fun parse(reader: StringReader?): String = reader?.readString() ?: ""

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(examples, builder)
    }

    override fun getExamples(): Collection<String> = availableRegions()

    companion object {
        fun register() = ArgumentTypes.register(
            "region",
            RegionArgumentType().javaClass,
            ConstantArgumentSerializer(Companion::type)
        )

        fun type() = RegionArgumentType()

        private fun availableRegions(): Set<String> = ClientWorldRegions.keys
    }
}
