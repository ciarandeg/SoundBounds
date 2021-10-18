package com.ciarandg.soundbounds.client.event.events

import com.ciarandg.soundbounds.client.ui.radial.BatonMenu
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec2f

object BatonMenuEvent {
    private var menu: BatonMenu? = null

    fun register() {
        KeyBindings.registerKeyBinding(BatonMenu.binding)
        TickEvent.PLAYER_POST.register {
            menu = if (!isBindingPressed()) null else BatonMenu(getMousePos())
            menu?.draw()
        }
    }

    private fun isBindingPressed() = with(MinecraftClient.getInstance()) {
        BatonMenu.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }

    private fun getMousePos() = with(MinecraftClient.getInstance().mouse) { Vec2f(x.toFloat(), y.toFloat())  }
}
