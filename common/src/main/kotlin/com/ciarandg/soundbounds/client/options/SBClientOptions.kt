package com.ciarandg.soundbounds.client.options

import com.google.gson.Gson
import java.io.File

object SBClientOptions {
    const val MAX_IDLE_DUR: Long = 60 * 1000 * 5
    const val MIN_IDLE_DUR: Long = 0
    const val IDLE_DUR_STEP: Long = 100

    private val dataFile = File("./config/sb-options.json")
    private val gson = Gson()
    var data: SBClientOptionsData = read()
        private set

    private fun read(): SBClientOptionsData {
        return if (dataFile.exists())
            gson.fromJson(dataFile.reader(), SBClientOptionsData::class.java)
        else SBClientOptionsData()
    }

    fun write() {
        dataFile.writeText(gson.toJson(data))
    }
}

data class SBClientOptionsData(
    var idleDuration: Long = 45000,
    var autoNowPlaying: Boolean = false
)
