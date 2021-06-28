package com.ciarandegroot.soundbounds.common.persistence

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.PersistentState

class WorldState(key: String?) : PersistentState(key) {
    private val regions: MutableMap<String, Region> = HashMap()

    override fun fromTag(tag: CompoundTag) {
        regions.putAll(tag.keys.associateWith { regionName -> Region.fromTag(tag.getCompound(regionName)) })
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        val newTag = CompoundTag()
        for (regionName in regions.keys)
            newTag.put(regionName, regions[regionName]?.toTag())
        return newTag
    }

    fun regionExists(regionName: String) = regions.containsKey(regionName)
    fun getRegion(regionName: String) = regions[regionName]
    fun getAllRegions() = regions.entries
    fun removeRegion(regionName: String): Region? {
        val r = regions.remove(regionName)
        if (r != null) this.markDirty()
        return r
    }
    fun putRegion(regionName: String, region: Region) {
        regions[regionName] = region
        this.markDirty()
    }
}