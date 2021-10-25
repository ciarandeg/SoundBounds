package com.ciarandg.soundbounds.client.ui.baton

import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
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
        private const val RAYCAST_RANGE = 1000.0

        fun fromPlayerRaycast(player: PlayerEntity, tickDelta: Float): ClientPositionMarker? {
            val cameraPos = player.getCameraPosVec(tickDelta)
            val rotation = player.getRotationVec(tickDelta)
            val endPoint = cameraPos.add(rotation.x * RAYCAST_RANGE, rotation.y * RAYCAST_RANGE, rotation.z * RAYCAST_RANGE)
            val hit = raycast(player, cameraPos, endPoint)
            // because the raycast will return a result that's right at the block face, we need to scale the vector
            // by a tiny bit to avoid rounding errors
            with(hit) {
                if (type == HitResult.Type.BLOCK) {
                    val linearDistance = pos.distanceTo(cameraPos)
                    val offsetPerBlock = 0.00001
                    val offsetScaled = 1.0 + linearDistance * offsetPerBlock
                    val hitBlock = BlockPos(hit.pos.subtract(cameraPos).multiply(offsetScaled).add(cameraPos))
                    return ClientPositionMarker(hitBlock)
                }
            }
            return null
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
