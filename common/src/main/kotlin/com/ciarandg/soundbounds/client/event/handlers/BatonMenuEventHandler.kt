package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.BatonMenu
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec2f

object BatonMenuEventHandler {
    private var menu: BatonMenu? = null

    fun register() {
        KeyBindings.registerKeyBinding(BatonMenu.binding)
        TickEvent.PLAYER_POST.register {
            menu = when {
                !isBindingPressed() -> null
                menu == null -> BatonMenu(getMousePos())
                else -> menu
            }
            menu?.draw()
        }
    }

    private fun isBindingPressed() = with(MinecraftClient.getInstance()) {
        BatonMenu.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }

    private fun getMousePos() = with(MinecraftClient.getInstance().mouse) { Vec2f(x.toFloat(), y.toFloat()) }
}
