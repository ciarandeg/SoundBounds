package com.ciarandg.soundbounds.server

import com.ciarandg.soundbounds.common.persistence.WorldState
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.server.world.ServerWorld
import java.lang.RuntimeException

object PersistenceUtils {
    private const val WORLD_REGIONS_KEY = "sb-data"
    private const val SERVER_METADATA_KEY = "sb-meta"

    fun getWorldState(world: ServerWorld): WorldState =
        world.persistentStateManager.getOrCreate({ WorldState(WORLD_REGIONS_KEY) }, WORLD_REGIONS_KEY)
    fun setWorldState(world: ServerWorld, state: WorldState) =
        world.persistentStateManager.set(state)
    fun getWorldRegion(world: ServerWorld, regionName: String) =
        getWorldState(world).getRegion(regionName)

    fun getServerMetaState(): ServerMetaState =
        getOverworldStateManager().getOrCreate(
            { ServerMetaState(SERVER_METADATA_KEY) },
            SERVER_METADATA_KEY
        )
    fun setServerMetaState(state: ServerMetaState) =
        getOverworldStateManager().set(state)

    private fun getOverworldStateManager() =
        getServer().overworld.persistentStateManager
    private fun getServer() =
        GameInstance.getServer() ?: throw RuntimeException("Must be run from server side")
}