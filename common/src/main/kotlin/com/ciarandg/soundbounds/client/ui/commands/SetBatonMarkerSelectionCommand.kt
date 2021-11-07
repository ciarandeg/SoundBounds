package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.client.ui.baton.BatonMarker
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.util.math.BlockPos

internal class SetBatonMarkerSelectionCommand(
    private val marker: BatonMarker,
    private val newPosition: BlockPos,
    private val state: PlayerBatonState,
    uncommitted: BlockTree
) : MacroSelectionCommand(
    listOf(
        lazy {
            object : ISelectionCommand {
                var oldPos: BlockPos? = null
                val getMarkerPos = when (marker) {
                    BatonMarker.FIRST -> state.selectionMode::getFirstMarkerPos
                    BatonMarker.SECOND -> state.selectionMode::getSecondMarkerPos
                }
                val setMarkerPos = when (marker) {
                    BatonMarker.FIRST -> state.selectionMode::setFirstMarker
                    BatonMarker.SECOND -> state.selectionMode::setSecondMarker
                }

                override fun execute() {
                    oldPos = getMarkerPos()
                    setMarkerPos(newPosition)
                }

                override fun unexecute() {
                    setMarkerPos(oldPos)
                }
            }
        },
        lazy { ClearSelectionCommand(uncommitted) },
        lazy { AddToSelectionCommand(uncommitted, state.selectionMode.getSelection().blockTree) },
    )
)
