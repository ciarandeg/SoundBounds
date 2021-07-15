package com.ciarandg.soundbounds.forge.client

import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.gui.screen.options.SoundOptionsScreen
import net.minecraft.sound.SoundCategory
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class ForgeClientEvents {
    @SubscribeEvent
    fun disableVanillaMusic(event: PlaySoundEvent) {
        // menu music doesn't trigger PlaySoundEvent for some reason, but it works in-game
        if (event.sound.category == SoundCategory.MUSIC)
            event.resultSound = null
    }

    @SubscribeEvent
    fun updateGameMusicVolume(event: GuiScreenEvent) {
        if (GameInstance.getClient().currentScreen is SoundOptionsScreen)
            GameMusicVolume.update()
    }
}