package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.ui.baton.cursor.Cursor
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    val cursor = Cursor()
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null
}
