package com.ciarandg.soundbounds.forge.common.item

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.regions.ClientRegionBounds
import com.ciarandg.soundbounds.client.regions.RegionSelection
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import me.shedaniel.architectury.platform.Platform
import me.shedaniel.architectury.utils.Env.CLIENT
import me.shedaniel.architectury.utils.Env.SERVER
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.NetherStarItem
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Baton(settings: Settings?) : NetherStarItem(settings) {
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

    override fun useOnBlock(context: ItemUsageContext?): ActionResult {
        if (context?.player?.itemsHand?.first()?.item is Baton) // only use when in right hand
            setCorner(Corner.SECOND)
        return super.useOnBlock(context)
    }

    private fun setCorner(corner: Corner) {
        val trace: HitResult? = when (Platform.getEnvironment()) {
            CLIENT -> MinecraftClient.getInstance().crosshairTarget
            SERVER -> null
            null -> null
        }
        if (trace !is BlockHitResult) return

        // useOnBlock (the right-click handler) gets called from several threads at once
        synchronized(this) {
            val currentTime = System.currentTimeMillis()
            val isCoolingDown = currentTime - corner.timestamp < cooldown
            val isNewBlock = !trace.blockPos.equals(corner.pos)
            if (!isCoolingDown || isNewBlock) {
                SoundBounds.LOGGER.info("Set corner $corner to ${trace.blockPos.toImmutable()}")
                corner.pos = trace.blockPos
                corner.timestamp = currentTime
                when (corner) {
                    Corner.FIRST -> ClientPlayerModel.marker1 = corner.pos
                    Corner.SECOND -> ClientPlayerModel.marker2 = corner.pos
                }
                with (ClientPlayerModel) {
                    uncommittedSelection = RegionSelection.fromBoxCorners(marker1, marker2)
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