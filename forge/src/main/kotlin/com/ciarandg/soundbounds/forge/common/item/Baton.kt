package com.ciarandg.soundbounds.forge.common.item

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.ui.baton.ClientPositionMarker
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.common.item.IBaton
import com.ciarandg.soundbounds.common.regions.blocktree.BlockTree
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.NetherStarItem
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Baton(settings: Settings?) : IBaton, NetherStarItem(settings) {
    companion object {
        private const val cooldown = 1000
    }

    private enum class Corner(var pos: BlockPos = BlockPos(0, 0, 0), var timestamp: Long = 0) {
        FIRST,
        SECOND
    }

    override fun canMine(state: BlockState?, world: World?, pos: BlockPos?, miner: PlayerEntity?): Boolean {
        return false
    }

    override fun onEntitySwing(stack: ItemStack?, entity: LivingEntity?): Boolean {
        if (entity != null &&
            entity.world.isClient &&
            entity is ClientPlayerEntity &&
            entity == GameInstance.getClient().player
        ) setCorner(Corner.FIRST)
        return super.onEntitySwing(stack, entity)
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient && hand == Hand.MAIN_HAND) setCorner(Corner.SECOND)
        return super.use(world, player, hand)
    }

    private fun setCorner(corner: Corner) {
        val newMarker = with(MinecraftClient.getInstance()) { ClientPositionMarker.fromPlayerRaycast(
            player ?: throw IllegalStateException(),
            tickDelta
        ) } ?: return
        synchronized(this) {
            val currentTime = System.currentTimeMillis()
            val isCoolingDown = currentTime - corner.timestamp < cooldown
            val markerPos = newMarker.getPos()
            val isNewBlock = markerPos != corner.pos
            if (!isCoolingDown || isNewBlock) {
                SoundBounds.LOGGER.info("Set corner $corner to ${newMarker.getPos()}")
                corner.pos = markerPos
                corner.timestamp = currentTime
                when (corner) {
                    Corner.FIRST -> ClientPlayerModel.batonState.marker1 = newMarker
                    Corner.SECOND -> ClientPlayerModel.batonState.marker2 = newMarker
                }
                with (ClientPlayerModel) {
                    uncommittedSelection = when (val marker1 = batonState.marker1) {
                        null -> when (val marker2 = batonState.marker2) {
                            null -> ClientRegionBounds()
                            else -> ClientRegionBounds(BlockTree.of(listOf(marker2.getPos())))
                        }
                        else -> when (val marker2 = batonState.marker2) {
                            null -> ClientRegionBounds(BlockTree.of(listOf(marker1.getPos())))
                            else -> ClientRegionBounds(BlockTree.fromBoxCorners(marker1.getPos(), marker2.getPos()))
                        }
                    }
                }
            }
        }
    }

    override fun appendTooltip(
        itemStack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        tooltipContext: TooltipContext?
    ) {
        tooltip?.replaceAll { TranslatableText("Bounds Baton") }
    }
}