package com.ciarandg.soundbounds.forge.common.item

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object SoundBoundsForgeItems {
    val registry: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, SoundBounds.MOD_ID)
    val baton = registry.register("bounds_baton") { Baton(Item.Settings().group(ItemGroup.TOOLS)) }
}