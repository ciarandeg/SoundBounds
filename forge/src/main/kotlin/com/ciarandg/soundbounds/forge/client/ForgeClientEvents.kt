package com.ciarandg.soundbounds.forge.client

import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.render.Renderer
import com.ciarandg.soundbounds.forge.common.item.Baton
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.options.SoundOptionsScreen
import net.minecraft.sound.SoundCategory
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
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

    @SubscribeEvent
    fun render(event: RenderWorldLastEvent) {
        val player = MinecraftClient.getInstance().player ?: return
        if (!player.itemsHand.any { it.item is Baton }) return

        // Offset by camera position, since it is reset by RenderWorldLastEvent
        val matrixStack = event.matrixStack
        val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        // Render player's bounds selection
        Renderer.renderPlayerMarkerSelection(matrixStack)
    }
}