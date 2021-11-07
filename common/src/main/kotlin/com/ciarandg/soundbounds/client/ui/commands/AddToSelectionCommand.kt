package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos

internal class AddToSelectionCommand(private val selection: BlockTree, private val toAdd: Set<BlockPos>) : ISelectionCommand {
    override fun execute() {
        selection.addAll(toAdd)
    }
    override fun unexecute() {
        selection.removeAll(toAdd)
    }
}
