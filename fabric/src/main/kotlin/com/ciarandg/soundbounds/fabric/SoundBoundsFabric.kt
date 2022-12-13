package com.ciarandg.soundbounds.fabric

import com.ciarandg.soundbounds.SoundBounds
import net.fabricmc.api.ModInitializer

class SoundBoundsFabric : ModInitializer {
    override fun onInitialize() {
        SoundBounds()
    }
}