package com.ciarandg.soundbounds.client.ui.baton.cursor

import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext

class Cursor {
    private var marker: ClientPositionMarker? = null
    internal val state = CursorState()

    fun getPos() = marker?.getPos()
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

    companion object {
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
