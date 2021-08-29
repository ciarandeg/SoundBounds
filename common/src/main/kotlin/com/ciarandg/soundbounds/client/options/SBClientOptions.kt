package com.ciarandg.soundbounds.client.options

import com.google.gson.Gson
import java.io.File

object SBClientOptions {
    const val MAX_FADE_DUR: Long = 8000
    const val MIN_FADE_DUR: Long = 100
    const val FADE_DUR_STEP: Long = 50
    const val MAX_IDLE_DUR: Long = 60 * 1000 * 5
    const val MIN_IDLE_DUR: Long = 0
    const val IDLE_DUR_STEP: Long = 100
    const val MAX_BUF_DUR: Long = 2500
    const val MIN_BUF_DUR: Long = 100
    const val MAX_LOOKAHEAD: Int = 10
    const val MIN_LOOKAHEAD: Int = 2

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

    fun setToDefault() {
        data = SBClientOptionsData()
    }
}

data class SBClientOptionsData(
    var fadeDuration: Long = 2000,
    var idleDuration: Long = 45000,
    var autoNowPlaying: Boolean = false,
    var bufferDuration: Long = 1000, // max duration of each buffer
    var lookahead: Int = 4 // how many buffers in advance should we be queueing new buffers?
)
