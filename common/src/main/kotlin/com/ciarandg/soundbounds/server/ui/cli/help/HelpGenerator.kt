package com.ciarandg.soundbounds.server.ui.cli.help

import com.ciarandg.soundbounds.plus
import com.ciarandg.soundbounds.server.ui.cli.Colors
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText

object HelpGenerator {
    fun readOut(node: HelpTreeNode, entries: MutableList<MutableText> = mutableListOf()): List<MutableText> {
        if (node.description != null) {
            entries.add(
                LiteralText("/${node.command}").formatted(Colors.Help.COMMAND) +
                    LiteralText(" - ").formatted(Colors.Help.SEPARATOR) +
                    LiteralText(node.description).formatted(Colors.Help.DESCRIPTION)
            )
        }

        for (n in node.children) readOut(n, entries)
        return entries
    }
}
