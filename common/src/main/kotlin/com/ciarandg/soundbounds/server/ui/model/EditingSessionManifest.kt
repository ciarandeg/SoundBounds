package com.ciarandg.soundbounds.server.ui.model

import com.ciarandg.soundbounds.server.ui.model.EditingSessionManifest.Companion.NewSessionResult.OTHER_ALREADY_EDITING_REQUESTED
import com.ciarandg.soundbounds.server.ui.model.EditingSessionManifest.Companion.NewSessionResult.REQUESTER_ALREADY_EDITING_OTHER
import com.ciarandg.soundbounds.server.ui.model.EditingSessionManifest.Companion.NewSessionResult.REQUESTER_ALREADY_EDITING_REQUESTED
import com.ciarandg.soundbounds.server.ui.model.EditingSessionManifest.Companion.NewSessionResult.SUCCESS
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import net.minecraft.server.network.ServerPlayerEntity

class EditingSessionManifest {
    private val entries: BiMap<ServerPlayerEntity, String> = HashBiMap.create()

    fun requestNewSession(player: ServerPlayerEntity, regionName: String): NewSessionResult = synchronized(this) {
        return when {
            entries[player] == regionName -> REQUESTER_ALREADY_EDITING_REQUESTED
            entries.containsKey(player) -> REQUESTER_ALREADY_EDITING_OTHER
            entries.containsValue(regionName) -> OTHER_ALREADY_EDITING_REQUESTED
            else -> {
                entries[player] = regionName
                SUCCESS
            }
        }
    }

    fun endSession(player: ServerPlayerEntity) = synchronized(this) { entries.remove(player) }
    fun endSession(regionName: String) = synchronized(this) { entries.inverse().remove(regionName) }

    companion object {
        enum class NewSessionResult {
            REQUESTER_ALREADY_EDITING_REQUESTED,
            REQUESTER_ALREADY_EDITING_OTHER,
            OTHER_ALREADY_EDITING_REQUESTED,
            SUCCESS
        }
    }
}
