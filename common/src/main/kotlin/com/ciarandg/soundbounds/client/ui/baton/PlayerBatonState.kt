package com.ciarandg.soundbounds.client.ui.baton

class PlayerBatonState {
    var commitMode = CommitMode.ADDITIVE
    var cursor: ClientPositionMarker? = null
    var marker1: ClientPositionMarker? = null
    var marker2: ClientPositionMarker? = null
}
