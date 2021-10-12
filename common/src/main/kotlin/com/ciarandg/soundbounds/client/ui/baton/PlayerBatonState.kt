package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.ui.baton.selection.ClientPositionMarker

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null
}
