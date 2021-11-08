package com.ciarandg.soundbounds.forge.common.item

import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.common.item.IBaton
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.block.BlockState
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
    private enum class Corner { FIRST, SECOND }

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

    private fun setCorner(corner: Corner) = synchronized(this) { when (corner) {
            Corner.FIRST -> ClientSelectionController.setFirstBatonMarker()
            Corner.SECOND -> ClientSelectionController.setSecondBatonMarker()
        } }

    override fun appendTooltip(
        itemStack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        tooltipContext: TooltipContext?
    ) {
        tooltip?.replaceAll { TranslatableText("Bounds Baton") }
    }
}