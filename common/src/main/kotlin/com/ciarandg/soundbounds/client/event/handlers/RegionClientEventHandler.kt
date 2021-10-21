package com.ciarandg.soundbounds.client.event.handlers

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.RegionSwitcher
import com.ciarandg.soundbounds.common.network.CreateRegionMessage
import com.ciarandg.soundbounds.common.network.CurrentRegionMessage
import com.ciarandg.soundbounds.common.network.RegionDestroyMessageS2C
import com.ciarandg.soundbounds.common.network.RegionUpdateMessageS2C
import com.ciarandg.soundbounds.common.network.VisualizeRegionMessageS2C
import me.shedaniel.architectury.event.events.TickEvent
import me.shedaniel.architectury.networking.NetworkManager

object RegionClientEventHandler {
    fun register() {
        // register RegionSwitcher update
        TickEvent.PLAYER_POST.register { if (it.world.isClient) RegionSwitcher.update() }

        // register region-management channels
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CREATE_REGION_CHANNEL_S2C,
            CreateRegionMessage()
        )
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.DESTROY_REGION_CHANNEL_S2C,
            RegionDestroyMessageS2C()
        )
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.UPDATE_REGIONS_CHANNEL_S2C,
            RegionUpdateMessageS2C()
        )

        // register channel for checking current region
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.CURRENT_REGION_CHANNEL_S2C,
            CurrentRegionMessage()
        )

        // register channel for visualizing a region
        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            SoundBounds.VISUALIZE_REGION_CHANNEL_S2C,
            VisualizeRegionMessageS2C()
        )
    }
}
