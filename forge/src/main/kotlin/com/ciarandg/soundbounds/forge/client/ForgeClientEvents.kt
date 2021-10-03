package com.ciarandg.soundbounds.forge.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.forge.client.render.Renderer
import com.mojang.blaze3d.systems.RenderSystem
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.options.SoundOptionsScreen
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexConsumers
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.Box
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
        val matrixStack = event.matrixStack
        val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos

        // Offset by camera position, since it is reset by RenderWorldLastEvent
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = source.getBuffer(RenderLayer.LINES)

        WorldRenderer.drawBox(
            matrixStack, buffer.vertexBuilder,
            Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
            1.0f, 1.0f, 1.0f, 1.0f
        )

        source.draw(RenderLayer.LINES)
    }
}