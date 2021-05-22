package com.ciarandegroot.soundbounds.forge

import com.ciarandegroot.soundbounds.SoundBounds
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(SoundBounds.MOD_ID)
class SoundBoundsForge {
    init {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SoundBounds.MOD_ID, FMLJavaModLoadingContext.get().modEventBus)
        SoundBounds()
    }

    companion object {
        val baton = SoundBounds.items.register("bounds_baton") { Item(Item.Properties().tab(CreativeModeTab.TAB_TOOLS)) }
    }
}