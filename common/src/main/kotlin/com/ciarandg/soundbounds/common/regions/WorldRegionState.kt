package com.ciarandg.soundbounds.common.regions

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.PersistentState

class WorldRegionState(key: String?) : PersistentState(key) {
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
    fun getRegion(regionName: String): Region? {
        this.markDirty()
        return regions[regionName]
    }
    fun getAllRegions(): Set<Map.Entry<String, Region>> {
        this.markDirty()
        return regions.entries
    }
    fun removeRegion(regionName: String): Region? {
        val r = regions.remove(regionName)
        if (r != null) this.markDirty()
        return r
    }
    fun putRegion(regionName: String, region: Region) {
        regions[regionName] = region
        this.markDirty()
    }

    companion object {
        private const val WORLD_REGIONS_KEY = "sb-regions"

        fun get(world: ServerWorld): WorldRegionState =
            world.persistentStateManager.getOrCreate({ WorldRegionState(WORLD_REGIONS_KEY) }, WORLD_REGIONS_KEY)
        fun set(world: ServerWorld, state: WorldRegionState) =
            world.persistentStateManager.set(state)
    }
}
