package com.ciarandg.soundbounds.server.ui.controller

import com.ciarandg.soundbounds.common.persistence.WorldState
import net.minecraft.server.world.ServerWorld

object Utils {
    private const val DATA_KEY = "sb-data"
    fun getWorldState(world: ServerWorld): WorldState =
        world.persistentStateManager.getOrCreate({ WorldState(DATA_KEY) }, DATA_KEY)
    fun setWorldState(world: ServerWorld, state: WorldState) =
        world.persistentStateManager.set(state)
    fun getWorldRegion(world: ServerWorld, regionName: String) =
        getWorldState(world).getRegion(regionName)
}