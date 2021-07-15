package com.ciarandg.soundbounds.client.audio

import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.sound.SoundCategory

object GameMusicVolume {
    private var masterVolume: Float = 0.0F
    private var musicVolume: Float = 0.0F

    fun get(): Float = masterVolume * musicVolume

    fun update() {
        val options = GameInstance.getClient().options
        masterVolume = options.getSoundVolume(SoundCategory.MASTER)
        musicVolume = options.getSoundVolume(SoundCategory.MUSIC)
    }
}
