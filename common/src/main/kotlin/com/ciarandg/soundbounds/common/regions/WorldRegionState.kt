package com.ciarandg.soundbounds.common.regions

import com.ciarandg.soundbounds.SoundBounds
import dev.architectury.utils.GameInstance
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.PersistentState
import java.io.File

class WorldRegionState : PersistentState() {
    private val regions: MutableMap<String, RegionData> = HashMap()

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        val newTag = NbtCompound()
        for (regionName in regions.keys)
            newTag.put(regionName, regions[regionName]?.toTag())
        return newTag
    }

    override fun save(file: File?) {
        SoundBounds.LOGGER.info("Saving WorldRegionState, ${regions.size} regions total")
        super.save(file)
    }

    fun regionExists(regionName: String) = regions.containsKey(regionName)
    fun getRegion(regionName: String): RegionData? {
        this.markDirty()
        return regions[regionName]
    }
    fun getAllRegions(): Set<Map.Entry<String, RegionData>> {
        this.markDirty()
        return regions.entries
    }
    fun removeRegion(regionName: String): RegionData? {
        val r = regions.remove(regionName)
        if (r != null) this.markDirty()
        return r
    }
    fun putRegion(regionName: String, data: RegionData) {
        regions[regionName] = data
        this.markDirty()
    }

    companion object {
        private const val WORLD_REGIONS_KEY = "sb-regions"

        private fun fromTag(tag: NbtCompound): WorldRegionState {
            val state = WorldRegionState()
            state.regions.putAll(tag.keys.associateWith { regionName -> RegionData.fromTag(tag.getCompound(regionName)) })
            return state
        }

        fun getOrCreate(world: ServerWorld): WorldRegionState {
            return world.persistentStateManager.getOrCreate({ nbt ->
                val state = fromTag(nbt)
                SoundBounds.LOGGER.info("Loaded WorldRegionState with ${state.regions.size} regions")
                state
            }, {
                SoundBounds.LOGGER.info("Creating empty WorldRegionState")
                WorldRegionState()
            },
                WORLD_REGIONS_KEY
            )
        }

        fun set(world: ServerWorld, state: WorldRegionState) =
            world.persistentStateManager.set(WORLD_REGIONS_KEY, state)
    }
}
