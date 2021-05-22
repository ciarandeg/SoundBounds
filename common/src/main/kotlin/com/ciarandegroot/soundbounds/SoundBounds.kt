package com.ciarandegroot.soundbounds

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry

class SoundBounds {
    init {
        items.register()
    }

    companion object {
        const val MOD_ID = "soundbounds"

        val items = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY)
    }
}