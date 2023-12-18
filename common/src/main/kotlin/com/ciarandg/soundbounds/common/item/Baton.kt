package com.ciarandg.soundbounds.common.item

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.common.network.PosMarkerUpdateMessage
import com.ciarandg.soundbounds.server.ui.cli.PosMarker
import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import dev.architectury.utils.Env.CLIENT
import dev.architectury.utils.Env.SERVER
import dev.architectury.utils.GameInstance
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.NetherStarItem
import net.minecraft.text.LiteralText
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

    fun onEntitySwing(entity: LivingEntity?) {
        if (entity != null &&
            entity.world.isClient &&
            entity is ClientPlayerEntity &&
            entity == GameInstance.getClient().player
        ) setCorner(Corner.FIRST)
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
                "Set corner $corner to ${trace.blockPos.toImmutable()}".let {
                    SoundBounds.LOGGER.info(it)
                    MinecraftClient.getInstance().player?.sendMessage(LiteralText(it), false)
                }
                corner.pos = trace.blockPos
                corner.timestamp = currentTime

                NetworkManager.sendToServer(
                    SoundBounds.POS_MARKER_UPDATE_CHANNEL_C2S,
                    PosMarkerUpdateMessage.buildBuffer(when (corner) {
                        Corner.FIRST -> PosMarker.FIRST
                        Corner.SECOND -> PosMarker.SECOND
                    }, corner.pos, false))
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