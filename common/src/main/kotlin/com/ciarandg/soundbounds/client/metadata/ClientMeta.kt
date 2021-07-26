package com.ciarandg.soundbounds.client.metadata

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.google.gson.Gson
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import java.io.InputStreamReader

object ClientMeta {
    private const val JSON_FILENAME = "sb.json"
    private val META_ID = Identifier(SoundBounds.MOD_ID, JSON_FILENAME)
    private var refreshedWhileNull = false // flag used to avoid logger spam
    var meta: JsonMeta? = null
        private set

    fun update() {
        val man = MinecraftClient.getInstance().resourceManager
        if (man.containsResource(META_ID)) {
            val stream = man.getResource(META_ID).inputStream
            val newMeta = Gson().fromJson(InputStreamReader(stream), JsonMeta::class.java)
            if (newMeta != meta) {
                meta = newMeta
                SoundBounds.LOGGER.info("Just updated local metadata!")
            }
        } else if (meta != null || !refreshedWhileNull) {
            refreshedWhileNull = true
            meta = null
            SoundBounds.LOGGER.warn("No local metadata available")
        }
    }
}
