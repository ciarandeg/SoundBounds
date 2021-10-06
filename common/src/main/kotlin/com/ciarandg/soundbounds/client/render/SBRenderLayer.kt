package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
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
        fun getLines(): RenderLayer {
            return of(
                "${SoundBounds.MOD_ID}_lines", VertexFormats.POSITION_COLOR, 1, 256,
                MultiPhaseParameters.builder()
                    .lineWidth(LineWidth(OptionalDouble.empty()))
                    .layering(VIEW_OFFSET_Z_LAYERING)
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
                    .transparency(NO_TRANSPARENCY)
                    .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
                    .lightmap(ENABLE_LIGHTMAP)
                    .overlay(ENABLE_OVERLAY_COLOR)
                    .build(true)
            return of(
                "${SoundBounds.MOD_ID}_selection_highlight", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                7, 256, true, false, multiPhaseParameters
            )
        }
    }
}
