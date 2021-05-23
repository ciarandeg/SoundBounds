package com.ciarandegroot.soundbounds.server.`interface`.cli

import com.ciarandegroot.soundbounds.common.command.ArgNodeData
import com.ciarandegroot.soundbounds.common.command.CommandNode
import com.ciarandegroot.soundbounds.common.command.LiteralNodeData
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import java.security.InvalidParameterException

object HelpGenerator {
    fun generate(root: CommandNode): List<MutableText> {
        val descriptions: MutableList<Pair<String, String?>> = MutableList(0) { Pair("", "") }
        parse(root, descriptions)
        return formatDescriptions(descriptions)
    }

    private fun parse(
        n: CommandNode,
        store: MutableList<Pair<String, String?>>,
        cmd: String = "",
        parentNodeHadWork: Boolean = false
    ) {
        var swap = "$cmd "
        swap += generateNodeText(n, parentNodeHadWork)

        when (n.data) {
            is LiteralNodeData -> store.add(Pair(swap, n.data.description))
            is ArgNodeData<*, *> -> store[store.lastIndex] = Pair(swap, store.last().second)
        }

        for (c in n.children) parse(c, store, swap, n.data.work != null)
    }

    private fun generateNodeText(n: CommandNode, isOptional: Boolean): String =
        when (n.data) {
            is LiteralNodeData -> n.data.literal
            is ArgNodeData<*, *> -> {
                val br = if (isOptional) "[]" else "<>"
                "${br[0]}${n.data.arg.name}${br[1]}"
            }
            else -> throw InvalidParameterException()
        }

    private fun formatDescriptions(descriptions: MutableList<Pair<String, String?>>) =
        descriptions.filter { entry -> entry.second != null }.map { l ->
            LiteralText("/${l.first}").formatted(Colors.Help.COMMAND) +
                    LiteralText(" - ").formatted(Colors.SEPARATOR) +
                    LiteralText(l.second).formatted(Colors.Help.DESCRIPTION)
        }
}

private operator fun MutableText.plus(text: MutableText) = append(text)
