package com.ciarandg.soundbounds.forge

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.forge.client.ForgeClientEvents
import com.ciarandg.soundbounds.forge.common.item.Baton
import com.ciarandg.soundbounds.forge.common.network.PosMarkerUpdateMessage
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(SoundBounds.MOD_ID)
class SoundBoundsForge {
    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SoundBounds.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)
        SoundBounds()

        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SOUNDBOUNDS_POS_MARKER_UPDATE_CHANNEL,
            PosMarkerUpdateMessage()
        )

        if (Platform.getEnv() == Dist.CLIENT)
            MinecraftForge.EVENT_BUS.register(ForgeClientEvents())
    }

    companion object {
        val baton = SoundBounds.items.register("bounds_baton") { Baton(Item.Settings().group(ItemGroup.TOOLS)) }
        val SOUNDBOUNDS_POS_MARKER_UPDATE_CHANNEL = Identifier(SoundBounds.MOD_ID, "marker_update")
    }
}