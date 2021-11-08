package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import java.lang.IllegalStateException

internal class ClearSelectionCommand(private val selection: BlockTree) : ISelectionCommand {
    private var beforeExecution: BlockTree? = null

    override fun execute() {
        beforeExecution = selection.copy()
        selection.clear()
    }

    override fun unexecute() {
        selection.addAll(beforeExecution ?: throw IllegalStateException("Must execute before unexecuting"))
    }
}
