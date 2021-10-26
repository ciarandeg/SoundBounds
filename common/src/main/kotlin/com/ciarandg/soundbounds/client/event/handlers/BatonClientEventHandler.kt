package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import com.ciarandg.soundbounds.client.ui.baton.PlayerBatonState
import com.ciarandg.soundbounds.client.ui.baton.modes.cursor.RadiusCursorMode
import com.ciarandg.soundbounds.client.ui.baton.modes.cursor.UnboundedCursorMode
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
import net.minecraft.util.math.Vec3d

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
                    val oldMode = cursorMode
                    when (PlayerBatonState.cursorModeBinding.isPressed) {
                        true -> if (oldMode !is RadiusCursorMode) {
                            val oldPosition = cursor?.getPos()
                            val newMode = RadiusCursorMode()
                            newMode.range = oldPosition?.let {
                                Vec3d.of(it).distanceTo(player.getCameraPosVec(MinecraftClient.getInstance().tickDelta))
                            } ?: RadiusCursorMode.DEFAULT_RANGE
                            cursorMode = newMode
                        }
                        false -> if (oldMode !is UnboundedCursorMode) { cursorMode = UnboundedCursorMode() }
                    }
                }
            }
        }
        ClientRawInputEvent.MOUSE_SCROLLED.register { client, delta ->
            val cursorMode = ClientPlayerModel.batonState.cursorMode
            if (client.currentScreen == null && cursorMode is RadiusCursorMode) {
                cursorMode.range += delta
                ActionResult.CONSUME
            } else ActionResult.PASS
        }
    }

    private fun shouldOpenMenu() = with(MinecraftClient.getInstance()) {
        BatonMenuScreen.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }
}
