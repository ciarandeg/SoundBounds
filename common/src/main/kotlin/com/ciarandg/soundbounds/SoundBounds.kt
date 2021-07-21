package com.ciarandg.soundbounds

import com.ciarandg.soundbounds.client.ClientEvents
import com.ciarandg.soundbounds.client.regions.ClientRegion
import com.ciarandg.soundbounds.common.CommonEvents
import com.ciarandg.soundbounds.common.regions.RegionData
import com.ciarandg.soundbounds.server.ServerEvents
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.registry.DeferredRegister
import net.fabricmc.api.EnvType
import net.minecraft.item.Item
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SoundBounds {
    init {
        CommonEvents.register()
        when (Platform.getEnv() ?: throw RuntimeException("Why would you ever have a null environment?")) {
            EnvType.CLIENT -> ClientEvents.register()
            EnvType.SERVER -> ServerEvents.register()
        }
    }

    companion object {
        const val MOD_ID = "soundbounds"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)

        val items: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY)
        val META_HASH_CHECK_S2C = Identifier(MOD_ID, "meta_hash_check_s2c")
        val META_HASH_CHECK_C2S = Identifier(MOD_ID, "meta_hash_check_c2s")
        val NOW_PLAYING_CHANNEL_C2S = Identifier(MOD_ID, "now_playing_c2s")
        val NOW_PLAYING_CHANNEL_S2C = Identifier(MOD_ID, "now_playing_s2c")
        val SYNC_METADATA_CHANNEL_C2S = Identifier(MOD_ID, "sync_metadata_c2s")
        val SYNC_METADATA_CHANNEL_S2C = Identifier(MOD_ID, "sync_metadata_s2c")
        val UPDATE_REGIONS_CHANNEL_S2C = Identifier(MOD_ID, "update_regions")
        val DESTROY_REGION_CHANNEL_S2C = Identifier(MOD_ID, "destroy_region")
    }
}

typealias RegionEntry = Pair<String, RegionData>
typealias ClientRegionEntry = Pair<String, ClientRegion>
operator fun MutableText.plus(text: MutableText): MutableText = append(text)
