package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.radial.baton.BatonMenuScreen
import com.ciarandg.soundbounds.common.item.IBaton
import com.ciarandg.soundbounds.common.network.CommitSelectionMessage
import com.ciarandg.soundbounds.common.network.SetBatonModeMessageS2C
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.MinecraftClient

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
    }

    private fun shouldOpenMenu() = with(MinecraftClient.getInstance()) {
        BatonMenuScreen.binding.isPressed &&
            player?.isHolding { it is IBaton } == true &&
            currentScreen == null
    }
}
