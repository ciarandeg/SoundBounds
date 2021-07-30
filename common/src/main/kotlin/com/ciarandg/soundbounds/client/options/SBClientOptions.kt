package com.ciarandg.soundbounds.client.options

import com.google.gson.Gson
import java.io.File

object SBClientOptions {
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
    var autoNowPlaying: Boolean = false
)
