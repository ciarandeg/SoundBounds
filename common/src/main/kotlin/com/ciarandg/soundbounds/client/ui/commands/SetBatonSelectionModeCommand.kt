package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.AbstractSelectionMode
import java.lang.IllegalStateException

internal class SetBatonSelectionModeCommand(private val mode: AbstractSelectionMode, private val state: PlayerBatonState) : ISelectionCommand {
    private var oldMode: AbstractSelectionMode? = null

    override fun execute() {
        oldMode = state.selectionMode
        state.selectionMode = mode
    }

    override fun unexecute() {
        state.selectionMode = oldMode ?: throw IllegalStateException("Must execute before unexecuting")
    }
}
