package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.ui.baton.cursor.Cursor
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.AbstractSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.BoxSelectionMode

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    val cursor = Cursor()
    var selectionMode: AbstractSelectionMode = BoxSelectionMode()
}
