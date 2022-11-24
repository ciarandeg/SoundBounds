package com.ciarandg.soundbounds.client.audio

import dev.architectury.utils.GameInstance
import net.minecraft.sound.SoundCategory
import java.util.Observable

object GameMusicVolume : Observable() {
    private var masterVolume: Float = 0.0F
    private var musicVolume: Float = 0.0F

    fun mixed(): Float = masterVolume * musicVolume

    fun update() {
        val options = GameInstance.getClient().options
        val oldMixed = mixed()
        masterVolume = options.getSoundVolume(SoundCategory.MASTER)
        musicVolume = options.getSoundVolume(SoundCategory.MUSIC)
        val newMixed = mixed()
        if (newMixed != oldMixed) {
            setChanged()
            notifyObservers(newMixed)
        }
    }
}
