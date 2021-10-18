package com.ciarandg.soundbounds.client.event.events

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.BatonMenu
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient

object BatonMenuEvent {
    fun register() {
        KeyBindings.registerKeyBinding(BatonMenu.binding)

        TickEvent.PLAYER_POST.register {
            val client = MinecraftClient.getInstance()
            if (
                BatonMenu.binding.isPressed &&
                client.player?.isHolding { it is IBaton } == true &&
                client.currentScreen == null
            )
                SoundBounds.LOGGER.info("Opening baton menu ${System.currentTimeMillis()}")
        }
    }
}