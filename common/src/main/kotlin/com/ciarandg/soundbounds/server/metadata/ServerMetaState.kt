package com.ciarandg.soundbounds.server.metadata

import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.google.gson.Gson
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.PersistentState
import net.minecraft.world.PersistentStateManager

class ServerMetaState(key: String?) : PersistentState(key) {
    var meta = JsonMeta()
        set(value) {
            field = value
            this.markDirty()
        }

    override fun fromTag(tag: CompoundTag?) {
        val metaTag = tag?.getString("meta") ?: return
        meta = gson.fromJson(metaTag, JsonMeta::class.java)
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        val newTag = CompoundTag()
        newTag.putString("meta", gson.toJson(meta))
        return newTag
    }

    companion object {
        private const val SERVER_METADATA_KEY = "sb-meta"
        val gson = Gson()

        fun get(): ServerMetaState =
            getStateManager().getOrCreate(
                { ServerMetaState(SERVER_METADATA_KEY) },
                SERVER_METADATA_KEY
            )
        fun set(state: ServerMetaState) =
            getStateManager().set(state)

        private fun getStateManager(): PersistentStateManager {
            val server = GameInstance.getServer() ?: throw RuntimeException("Must be run from server side")
            return server.overworld.persistentStateManager
        }
    }
}
