package com.ciarandg.soundbounds.client

import java.util.Observable

object ClientTicker : Observable() {
    internal fun tick() {
        setChanged()
        notifyObservers()
    }
}
