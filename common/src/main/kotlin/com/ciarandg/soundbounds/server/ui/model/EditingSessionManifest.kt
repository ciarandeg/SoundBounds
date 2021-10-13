package com.ciarandg.soundbounds.server.ui.model

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import net.minecraft.server.network.ServerPlayerEntity
import java.security.InvalidParameterException

class EditingSessionManifest {
    private val entries: BiMap<ServerPlayerEntity, String> = HashBiMap.create()

    fun requestNewSession(player: ServerPlayerEntity, regionName: String) = synchronized(this) {
        when {
            entries[player] == regionName -> throw InvalidParameterException("$player is already editing region $regionName")
            entries.containsKey(player) -> throw InvalidParameterException("$player is currently editing region ${entries[player]}")
            entries.containsValue(regionName) -> throw InvalidParameterException("Region $regionName is currently being edited by another player")
            else -> entries[player] = regionName
        }
    }

    fun endSession(player: ServerPlayerEntity) = synchronized(this) { entries.remove(player) }
    fun endSession(regionName: String) = synchronized(this) { entries.inverse().remove(regionName) }
}
