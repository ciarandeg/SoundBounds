package com.ciarandg.soundbounds.client.ui.baton.modes.selection

import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3i

// Intended for selection modes that reshape the uncommitted selection
abstract class SculptingSelectionMode : AbstractSelectionMode() {
    override fun renderMarkers(matrixStack: MatrixStack) {
        super.renderMarkers(matrixStack)
        marker1?.getPos()?.let { m1 ->
            marker2?.getPos()?.let { m2 ->
                drawGuideLine(matrixStack.peek().model, m1, m2)
            }
        }
    }

    private fun drawGuideLine(matrixPos: Matrix4f, from: Vec3i, to: Vec3i) {
        val source = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
        val buffer = source.getBuffer(SBRenderLayer.getThickLines())
        val color = RenderColor.GREEN
        buffer.vertex(matrixPos, from.x.toFloat() + 0.5f, from.y.toFloat() + 0.5f, from.z.toFloat() + 0.5f).color(color.red, color.green, color.blue, color.alpha).next()
        buffer.vertex(matrixPos, to.x.toFloat() + 0.5f, to.y.toFloat() + 0.5f, to.z.toFloat() + 0.5f).color(color.red, color.green, color.blue, color.alpha).next()
        source.draw()
    }
}
