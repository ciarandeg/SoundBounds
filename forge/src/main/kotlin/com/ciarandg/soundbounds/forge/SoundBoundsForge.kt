package com.ciarandg.soundbounds.forge

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.forge.client.ForgeClientEvents
import com.ciarandg.soundbounds.forge.common.item.SoundBoundsForgeItems
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.platform.forge.EventBuses
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