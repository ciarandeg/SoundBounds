package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.client.ui.radial.BatonMenuScreen
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec2f

object BatonMenuEventHandler {
    private var menu: BatonMenuScreen? = null

    fun register() {
        KeyBindings.registerKeyBinding(BatonMenuScreen.binding)
        TickEvent.PLAYER_POST.register {
            if (isBindingPressed() && menu == null)
                MinecraftClient.getInstance().openScreen(BatonMenuScreen())
        }
    }

    private fun isBindingPressed() = with(MinecraftClient.getInstance()) {
        BatonMenuScreen.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }

    private fun getMousePos() = with(MinecraftClient.getInstance().mouse) { Vec2f(x.toFloat(), y.toFloat()) }
}
