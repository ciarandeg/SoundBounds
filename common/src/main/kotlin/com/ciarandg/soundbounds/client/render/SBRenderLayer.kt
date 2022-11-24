package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_QUADS
import java.util.OptionalDouble

@Suppress("INACCESSIBLE_TYPE")
class SBRenderLayer(
    name: String,
    vertexFormat: VertexFormat,
    drawMode: DrawMode,
    expectedBufferSize: Int,
    hasCrumbling: Boolean,
    translucent: Boolean,
    startAction: () -> Unit,
    endAction: () -> Unit
) : RenderLayer(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction) {
    companion object {
        fun getThinLines() = getLines(3.0)
        fun getThickLines() = getLines(5.0)
        fun getLines(lineWidth: Double): RenderLayer {
            TODO()
            // return of(
            //     "${SoundBounds.MOD_ID}_lines", VertexFormats.POSITION_COLOR, GL_LINES, 256,
            //     MultiPhaseParameters.builder()
            //         .lineWidth(LineWidth(OptionalDouble.of(lineWidth)))
            //         .layering(VIEW_OFFSET_Z_LAYERING)
            //         .transparency(TRANSLUCENT_TRANSPARENCY)
            //         .target(ITEM_TARGET)
            //         .writeMaskState(ALL_MASK)
            //         .build(false)
            // )
        }

        fun getSelectionHighlight(texture: Identifier): RenderLayer {
            TODO()
            // val multiPhaseParameters: MultiPhaseParameters =
            //     MultiPhaseParameters.builder()
            //         .texture(Texture(texture, false, false))
            //         .transparency(ADDITIVE_TRANSPARENCY)
            //         .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
            //         .cull(DISABLE_CULLING)
            //         .build(true)
            // return of(
            //     "${SoundBounds.MOD_ID}_selection_highlight", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            //     GL_QUADS, 256, true, false, multiPhaseParameters
            // )
        }
    }
}
