package com.ciarandg.soundbounds.client.ui.baton.cursor

import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker

class Cursor {
    private var marker: ClientPositionMarker? = null
    internal val state = CursorState()

    fun getPos() = marker?.getPos()
    fun getMarker() = marker
    fun setMarker(marker: ClientPositionMarker) {
        this.marker = marker
    }
    fun clearMarker() {
        marker = null
    }
}
