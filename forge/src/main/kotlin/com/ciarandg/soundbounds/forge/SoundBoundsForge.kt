package com.ciarandg.soundbounds.forge

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.forge.client.ForgeClientEvents
import com.ciarandg.soundbounds.forge.common.item.SoundBoundsForgeItems
import com.ciarandg.soundbounds.common.network.PosMarkerUpdateMessage
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.platform.Platform
import dev.architectury.platform.forge.EventBuses
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(SoundBounds.MOD_ID)
class SoundBoundsForge {
    init {
        val bus = FMLJavaModLoadingContext.get().modEventBus
        EventBuses.registerModEventBus(SoundBounds.MOD_ID, bus) // register Forge event bus with Architectury
        SoundBounds()

        SoundBoundsForgeItems.registry.register(bus)

        if (Platform.getEnv() == Dist.CLIENT)
            ClientLifecycleEvent.CLIENT_SETUP.register {
                MinecraftForge.EVENT_BUS.register(ForgeClientEvents())
            }
    }
}