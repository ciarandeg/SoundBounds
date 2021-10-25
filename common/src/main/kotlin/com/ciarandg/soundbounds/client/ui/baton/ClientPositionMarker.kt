package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext

class ClientPositionMarker(position: BlockPos) {
    val bounds = ClientRegionBounds(BlockTree.of(listOf(position)))
    fun getPos() = bounds.blockTree.first()

    companion object {
        fun fromPlayerRaycast(player: PlayerEntity, tickDelta: Float, cursorMode: CursorMode = ClientPlayerModel.batonState.cursorMode): ClientPositionMarker? {
            val cameraPos = player.getCameraPosVec(tickDelta)
            val rotation = player.getRotationVec(tickDelta)
            val range = cursorMode.range
            val endPoint = cameraPos.add(rotation.x * range, rotation.y * range, rotation.z * range)
            val hit = raycast(player, cameraPos, endPoint)
            return when (cursorMode) {
                CursorMode.RADIUS -> ClientPositionMarker(BlockPos(endPoint))
                CursorMode.UNBOUNDED -> with(hit) {
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
