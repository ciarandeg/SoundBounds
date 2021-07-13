package com.ciarandg.soundbounds.server.metadata

import com.ciarandg.soundbounds.common.metadata.JsonMeta
import com.google.gson.Gson
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.PersistentState

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
        val gson = Gson()
    }
}