package com.ciarandegroot.soundbounds.forge

import com.ciarandegroot.soundbounds.SoundBounds
import com.ciarandegroot.soundbounds.forge.common.item.Baton
import me.shedaniel.architectury.platform.forge.EventBuses
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
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
        val baton = SoundBounds.items.register("bounds_baton") { Baton(Item.Settings().group(ItemGroup.TOOLS)) }
    }
}