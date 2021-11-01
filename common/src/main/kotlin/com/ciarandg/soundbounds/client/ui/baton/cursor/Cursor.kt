package com.ciarandg.soundbounds.client.ui.baton.cursor

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer
import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.render.SBRenderLayer
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import org.lwjgl.glfw.GLFW

class Cursor {
    private var marker: ClientPositionMarker? = null
    private val state = CursorState()

    fun isBounded() = state.isBounded
    fun unbind() = state.unbind()
    fun bindToCurrentRadius(player: PlayerEntity, tickDelta: Float) {
        if (isBounded()) return
        val currentRadius = marker?.getPos()?.let {
            Vec3d.of(it).distanceTo(player.getCameraPosVec(tickDelta))
        } ?: CursorState.DEFAULT_RANGE_BOUNDED
        bindToRadius(currentRadius)
    }
    fun incrementRadius(increment: Double) =
        state.incrementBoundedRadius(increment)
    private fun bindToRadius(radius: Double) {
        state.setBoundedRadius(radius)
        state.bind()
    }

    fun getMarker() = marker
    fun clearMarker() {
        marker = null
    }
    fun setMarkerFromRaycast(player: PlayerEntity, tickDelta: Float) {
        val cameraPos = player.getCameraPosVec(tickDelta)
        val rotation = player.getRotationVec(tickDelta)
        val range = state.range
        val endPoint = cameraPos.add(rotation.x * range, rotation.y * range, rotation.z * range)
        val hit = raycast(player, cameraPos, endPoint)
        marker = when (state.isBounded) {
            true -> ClientPositionMarker(BlockPos(endPoint))
            false -> with(hit) {
                if (type == HitResult.Type.BLOCK) {
                    // because the raycast will return a result that's right at the block face, we need to scale the vector
                    // by a tiny bit to avoid rounding errors
                    val linearDistance = pos.distanceTo(cameraPos)
                    val offsetPerBlock = 0.00001
                    val offsetScaled = 1.0 + linearDistance * offsetPerBlock
                    val hitBlock = BlockPos(hit.pos.subtract(cameraPos).multiply(offsetScaled).add(cameraPos))
                    ClientPositionMarker(hitBlock)
                } else null
            }
        }
    }

    fun render(matrixStack: MatrixStack) {
        marker?.let {
            RegionVisualizationRenderer.renderFilledWireframe(
                matrixStack, it.bounds, cursorTexture,
                SBRenderLayer.getThickLines(), wireframeColor
            )
        }
    }

    companion object {
        private val wireframeColor = RenderColor.WHITE
        val unbindBinding = KeyBinding("Baton Range Modifier", GLFW.GLFW_KEY_LEFT_CONTROL, SoundBounds.KEYBIND_CATEGORY)
        private val cursorTexture = Identifier(SoundBounds.MOD_ID, "textures/entity/cursor.png")

        private fun raycast(entity: Entity, startPoint: Vec3d, endPoint: Vec3d) =
            entity.world.raycast(
                RaycastContext(
                    startPoint, endPoint,
                    RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE,
                    entity
                )
            )
    }
}
