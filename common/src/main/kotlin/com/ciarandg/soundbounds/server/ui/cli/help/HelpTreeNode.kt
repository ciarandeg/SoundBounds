package com.ciarandg.soundbounds.server.ui.cli.help

import com.ciarandg.soundbounds.common.command.ArgNodeData
import com.ciarandg.soundbounds.common.command.CommandNode
import com.ciarandg.soundbounds.common.command.LiteralNodeData
import java.security.InvalidParameterException

class HelpTreeNode(
    val commandNode: CommandNode,
    commandPrefix: String = "",
    parentDescription: String? = null,
    isOptionalArg: Boolean = false
) {
    val command: String = commandPrefix + when (commandNode.data) {
        is LiteralNodeData -> commandNode.data.literal
        is ArgNodeData<*, *> -> {
            val br = if (isOptionalArg) "[]" else "<>"
            "${br[0]}${commandNode.data.arg.name}${br[1]}"
        }
        else -> throw InvalidParameterException("Invalid CommandNode")
    }
    private val inheritableDescription: String? = when (commandNode.data) {
        is LiteralNodeData -> commandNode.data.description
        else -> parentDescription
    }
    val description: String? = if (commandNode.children.isEmpty()) inheritableDescription else null
    val children: List<HelpTreeNode>

    init {
        val mChildren = mutableListOf<HelpTreeNode>()
        for (n in commandNode.children) {
            mChildren.add(HelpTreeNode(n, "$command ", inheritableDescription, commandNode.data.work != null))
        }
        children = mChildren
    }
}