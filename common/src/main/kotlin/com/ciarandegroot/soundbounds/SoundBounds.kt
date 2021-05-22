package com.ciarandegroot.soundbounds

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

class SoundBounds {
    init {
        items.register()
    }

    companion object {
        const val MOD_ID = "soundbounds"

        val items: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY)
    }
}