package com.ciarandg.soundbounds.forge.client

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.audio.GameMusicVolume
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.forge.common.item.Baton
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.options.SoundOptionsScreen
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.WorldRenderer
import net.minecraft.item.Item
import net.minecraft.sound.SoundCategory
import net.minecraft.util.math.BlockPos
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

        fun makeBox(pos: BlockPos) = Box(
            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
            pos.x.toDouble() + 1.0,pos.y.toDouble() + 1.0,pos.z.toDouble() + 1.0
        ).expand(0.0001)

        val firstBox = ClientPlayerModel.marker1?.let { makeBox(it) }
        val secondBox = ClientPlayerModel.marker2?.let { makeBox(it) }

        val boxToDraw = when {
            firstBox == null && secondBox == null -> null
            firstBox != null && secondBox != null -> firstBox.union(secondBox)
            firstBox == null -> secondBox
            else -> firstBox
        }

        if (boxToDraw != null) WorldRenderer.drawBox(
            matrixStack, buffer.vertexBuilder,
            boxToDraw,
            1.0f, 0.0f, 1.0f, 1.0f
        )

        source.draw(RenderLayer.LINES)
    }
}