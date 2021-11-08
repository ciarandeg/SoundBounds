package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos

internal class RemoveFromSelectionCommand(private val selection: BlockTree, private val toRemove: Set<BlockPos>) : ISelectionCommand {
    override fun execute() {
        selection.removeAll(toRemove)
    }

    override fun unexecute() {
        selection.addAll(toRemove)
    }
}
