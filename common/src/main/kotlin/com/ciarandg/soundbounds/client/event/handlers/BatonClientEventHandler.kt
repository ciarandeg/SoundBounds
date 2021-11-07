package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.cursor.Cursor
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.baton.BatonMenuScreen
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientRawInputEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW

object BatonClientEventHandler {
    fun register() {
        // register baton menu open
        ClientRawInputEvent.MOUSE_CLICKED_POST.register { client, key, action, _ ->
            if (
                key == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS &&
                client.player?.isHolding { it is IBaton } == true && client.currentScreen == null
            ) {
                client.openScreen(BatonMenuScreen())
                ActionResult.CONSUME
            } else ActionResult.PASS
        }

        // register cursor update on tick
        TickEvent.PLAYER_POST.register { player ->
            val tickDelta = MinecraftClient.getInstance().tickDelta
            if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton)
                ClientSelectionController.setCursorPosition(player, tickDelta)
        }

        // register baton cursor bind/unbind
        KeyBindings.registerKeyBinding(Cursor.unbindBinding)
        TickEvent.PLAYER_POST.register { player ->
            if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton) {
                if (Cursor.unbindBinding.isPressed) ClientSelectionController.bindCursor(player, MinecraftClient.getInstance().tickDelta)
                else ClientSelectionController.unbindCursor()
            }
        }

        // register range adjustment with scroll wheel for bounded cursor
        ClientRawInputEvent.MOUSE_SCROLLED.register { client, delta ->
            if (client.currentScreen == null && ClientSelectionController.isCursorBounded()) {
                ClientSelectionController.incrementCursorRadius(delta)
                ActionResult.CONSUME
            } else ActionResult.PASS
        }

        // register commit selection with enter key
        val commitBinding = KeyBinding("Commit Baton Selection", GLFW.GLFW_KEY_ENTER, SoundBounds.KEYBIND_CATEGORY)
        KeyBindings.registerKeyBinding(commitBinding)
        TickEvent.PLAYER_POST.register { player ->
            if (commitBinding.isPressed && player.isHolding { it is IBaton }) { ClientSelectionController.commit() }
        }
    }
}
