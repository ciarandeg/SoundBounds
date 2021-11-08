package com.ciarandg.soundbounds.client.ui.commands

import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import java.lang.IllegalStateException

internal class SetCommitModeCommand(private val mode: CommitMode, private val state: PlayerBatonState) : ISelectionCommand {
    private var oldMode: CommitMode? = null

    override fun execute() {
        oldMode = state.commitMode
        state.commitMode = mode
    }

    override fun unexecute() {
        state.commitMode = oldMode ?: throw IllegalStateException("Must execute before unexecuting")
    }
}
