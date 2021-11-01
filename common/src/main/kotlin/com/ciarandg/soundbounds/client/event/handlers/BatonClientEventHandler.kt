package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.client.ui.baton.cursor.Cursor
import com.ciarandg.soundbounds.client.ui.radial.baton.BatonMenuScreen
import com.ciarandg.soundbounds.common.item.IBaton
import com.ciarandg.soundbounds.common.network.CommitSelectionMessage
import com.ciarandg.soundbounds.common.network.SetBatonModeMessageS2C
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.event.events.client.ClientRawInputEvent
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW

object BatonClientEventHandler {
    fun register() {
        // register channel for changing baton commit mode
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.SET_BATON_MODE_CHANNEL_S2C,
            SetBatonModeMessageS2C()
        )

        // register channel for committing baton selection
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.COMMIT_SELECTION_CHANNEL_S2C,
            CommitSelectionMessage()
        )

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
            with(ClientPlayerModel.batonState.cursor) {
                if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton)
                    setMarkerFromRaycast(player, tickDelta)
                else clearMarker()
            }
        }

        // register baton cursor bind/unbind
        KeyBindings.registerKeyBinding(Cursor.unbindBinding)
        TickEvent.PLAYER_POST.register { player ->
            if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton) {
                with(ClientPlayerModel.batonState.cursor) {
                    when (Cursor.unbindBinding.isPressed) {
                        true -> if (!isBounded())
                            bindToCurrentRadius(player, MinecraftClient.getInstance().tickDelta)
                        false -> unbind()
                    }
                }
            }
        }

        // register range adjustment with scroll wheel for bounded cursor
        ClientRawInputEvent.MOUSE_SCROLLED.register { client, delta ->
            with(ClientPlayerModel.batonState.cursor) {
                if (client.currentScreen == null && isBounded()) {
                    incrementRadius(delta)
                    ActionResult.CONSUME
                } else ActionResult.PASS
            }
        }
    }
}
