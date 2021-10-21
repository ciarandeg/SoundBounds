package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.client.options.SBOptionsScreen
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient

object OptionsScreenClientEventHandler {
    fun register() {
        KeyBindings.registerKeyBinding(SBOptionsScreen.binding)

        TickEvent.PLAYER_POST.register {
            val client = MinecraftClient.getInstance()
            if (SBOptionsScreen.binding.isPressed && client.currentScreen == null)
                client.openScreen(SBOptionsScreen())
        }
    }
}