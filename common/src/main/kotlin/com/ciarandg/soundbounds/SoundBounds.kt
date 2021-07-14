package com.ciarandg.soundbounds

import com.ciarandg.soundbounds.client.metadata.ClientMeta
import com.ciarandg.soundbounds.common.ui.cli.argument.PTArgumentType
import com.ciarandg.soundbounds.common.network.MetadataSyncMessage
import com.ciarandg.soundbounds.common.ui.cli.SoundBoundsCommand
import com.ciarandg.soundbounds.server.ServerUtils
import com.ciarandg.soundbounds.server.ui.controller.PlayerController
import me.shedaniel.architectury.event.events.CommandRegistrationEvent
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.event.events.PlayerEvent
import me.shedaniel.architectury.networking.NetworkManager
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.registry.DeferredRegister
import net.fabricmc.api.EnvType
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SoundBounds {
    init {
        // Item registration
        items.register()

        // Command registration
        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }

        // MVC setup
        PlayerEvent.PLAYER_JOIN.register {
            ServerUtils.playerControllers.putIfAbsent( it, PlayerController(it) )
        }

        // Metadata sync/update util registration
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            SYNC_METADATA_CHANNEL_C2S,
            MetadataSyncMessage()
        )
        if (Platform.getEnv() == EnvType.CLIENT) {
            GuiEvent.INIT_POST.register { _, _, _ -> ClientMeta.update() }
            NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                SYNC_METADATA_CHANNEL_S2C,
                MetadataSyncMessage()
            )
        }
    }

    companion object {
        const val MOD_ID = "soundbounds"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)

        val items: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY)
        val SYNC_METADATA_CHANNEL_C2S = Identifier(MOD_ID, "sync_metadata_c2s")
        val SYNC_METADATA_CHANNEL_S2C = Identifier(MOD_ID, "sync_metadata_s2c")
    }
}