package com.ciarandegroot.soundbounds

import com.ciarandegroot.soundbounds.common.command.SoundBoundsCommand
import com.ciarandegroot.soundbounds.common.command.argument.PTArgumentType
import me.shedaniel.architectury.event.events.CommandRegistrationEvent
import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SoundBounds {
    init {
        items.register()

        PTArgumentType.register()
        CommandRegistrationEvent.EVENT.register { dispatcher, _ -> SoundBoundsCommand.register(dispatcher) }
    }

    companion object {
        const val MOD_ID = "soundbounds"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)

        val items: DeferredRegister<Item> = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY)
    }
}