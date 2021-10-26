package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
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

        // register binding for opening baton menu
        KeyBindings.registerKeyBinding(BatonMenuScreen.binding)
        TickEvent.PLAYER_POST.register {
            with(MinecraftClient.getInstance()) {
                if (shouldOpenMenu() && currentScreen == null) openScreen(BatonMenuScreen())
            }
        }

        // register cursor update on tick
        TickEvent.PLAYER_POST.register { player ->
            with(MinecraftClient.getInstance()) {
                ClientPlayerModel.batonState.cursor =
                    if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton)
                        ClientPositionMarker.fromPlayerRaycast(player, tickDelta)
                    else null
            }
        }

        KeyBindings.registerKeyBinding(PlayerBatonState.cursorModeBinding)
        TickEvent.PLAYER_POST.register { player ->
            if (player.getStackInHand(Hand.MAIN_HAND).item is IBaton) {
                with(ClientPlayerModel.batonState) {
                    when (PlayerBatonState.cursorModeBinding.isPressed) {
                        true -> if (!isCursorBounded())
                            bindCursorToCurrentRadius(player, MinecraftClient.getInstance().tickDelta)
                        false -> unbindCursor()
                    }
                }
            }
        }
        ClientRawInputEvent.MOUSE_SCROLLED.register { client, delta ->
            with(ClientPlayerModel.batonState) {
                if (client.currentScreen == null && isCursorBounded()) {
                    incrementCursorRadius(delta)
                    ActionResult.CONSUME
                } else ActionResult.PASS
            }
        }
    }

    private fun shouldOpenMenu() = with(MinecraftClient.getInstance()) {
        BatonMenuScreen.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }
}
