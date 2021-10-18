package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_QUADS
import java.util.OptionalDouble

@Suppress("INACCESSIBLE_TYPE")
class SBRenderLayer(
    name: String,
    vertexFormat: VertexFormat,
    drawMode: Int,
    expectedBufferSize: Int,
    hasCrumbling: Boolean,
    translucent: Boolean,
    startAction: () -> Unit,
    endAction: () -> Unit
) : RenderLayer(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction) {
    companion object {
        fun getThinLines() = getLines(4.0)
        fun getThickLines() = getLines(5.0)
        fun getLines(lineWidth: Double): RenderLayer {
            return of(
                "${SoundBounds.MOD_ID}_lines", VertexFormats.POSITION_COLOR, GL_LINES, 256,
                MultiPhaseParameters.builder()
                    .lineWidth(LineWidth(OptionalDouble.of(lineWidth)))
                    .layering(VIEW_OFFSET_Z_LAYERING)
                    .depthTest(ALWAYS_DEPTH_TEST)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .target(ITEM_TARGET)
                    .writeMaskState(ALL_MASK)
                    .build(false)
            )
        }

        fun getSelectionHighlight(texture: Identifier): RenderLayer {
            val multiPhaseParameters: MultiPhaseParameters =
                MultiPhaseParameters.builder()
                    .texture(Texture(texture, false, false))
                    .transparency(ADDITIVE_TRANSPARENCY)
                    .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
                    .cull(DISABLE_CULLING)
                    .build(true)
            return of(
                "${SoundBounds.MOD_ID}_selection_highlight", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                GL_QUADS, 256, true, false, multiPhaseParameters
            )
        }

        fun getBatonRadialMenu(texture: Identifier): RenderLayer {
            val transparency = Transparency(
                "${SoundBounds.MOD_ID}_src_rgba_transparency",
                {
                    RenderSystem.enableBlend()
                    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
                },
                {
                    RenderSystem.disableBlend()
                    RenderSystem.defaultBlendFunc()
                }
            )
            val multiPhaseParameters: MultiPhaseParameters =
                MultiPhaseParameters.builder()
                    .texture(Texture(texture, false, false))
                    .transparency(transparency)
                    .build(true)
            return of(
                "${SoundBounds.MOD_ID}_radial_menu", VertexFormats.POSITION_TEXTURE,
                GL_QUADS, 256, true, false, multiPhaseParameters
            )
        }
    }
}
