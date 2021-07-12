package com.ciarandg.soundbounds.common.metadata

import com.ciarandg.soundbounds.SoundBounds
import com.google.gson.Gson
import me.shedaniel.architectury.platform.Platform
import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import java.io.InputStreamReader
import java.lang.RuntimeException

// A nice big metadata singleton
// On the client, the data is provided by a local resource pack
// On the server, the data is provided via `/sb sync-meta`
// The server's dataset is treated as the master copy
object SBMeta {
    private const val JSON_FILENAME = "sb.json"
    private val META_ID = Identifier(SoundBounds.MOD_ID, JSON_FILENAME)
    private var refreshedWhileNull = false // flag used to avoid logger spam on client
    var meta: JsonMeta? = null
        private set

    fun refreshClientMeta() {
        if (Platform.getEnv() != EnvType.CLIENT)
            throw RuntimeException("Can only fetch resource pack data from client side")

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

    fun setServerMeta(meta: JsonMeta) {
        if (Platform.getEnv() != EnvType.SERVER)
            throw RuntimeException("Can only set metadata directly when on server side")
        this.meta = meta
    }
}