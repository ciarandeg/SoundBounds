package com.ciarandegroot.soundbounds.server.ui.cli

import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText

object HelpGenerator {
    fun generate(node: HelpTreeNode, entries: MutableList<MutableText> = mutableListOf()): List<MutableText> {
        if (node.description != null) {
            entries.add(
                LiteralText("/${node.command}").formatted(Colors.Help.COMMAND) +
                        LiteralText(" - ").formatted(Colors.SEPARATOR) +
                        LiteralText(node.description).formatted(Colors.Help.DESCRIPTION)
            )
        }

        for (n in node.children) generate(n, entries)
        return entries
    }

    private operator fun MutableText.plus(text: MutableText) = append(text)
}