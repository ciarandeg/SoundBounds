package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.Fader
import com.ciarandg.soundbounds.common.regions.Region
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.lang.RuntimeException
import kotlin.math.floor

typealias RegionEntry = Pair<String, Region>

object RegionSwitcher {
    private val playerData = PlayerData(null, null) // used to prevent unnecessary iteration
    private val swapper = Swapper<RegionEntry>() // determines which region the player is hearing music from
    private val fader = Fader({}, { swapper.push() })

    fun update() {
        val oldPos = playerData.blockPos
        playerData.updatePos()
        if (playerData.blockPos == oldPos) return
        updateRegion()
    }

    private fun updateRegion() {
        val newRegion = searchPlayerRegion()
        if (newRegion != playerData.region) {
            playerData.region = newRegion
            swapper.submit(newRegion)
            if (newRegion != swapper.current)
                fader.requestFade()
            else fader.reset()
        }
    }

    private fun searchPlayerRegion(): RegionEntry? {
        val player = getPlayer()
        var match: RegionEntry? = null
        ClientWorldRegions.entries.forEach { mapEntry ->
            val containsPlayer = mapEntry.value.volumes.any {
                Box(it.first, it.second).contains(player)
            }
            val previousBest = match?.second?.priority
            val hasPriority = previousBest == null || mapEntry.value.priority > previousBest
            if (containsPlayer && hasPriority) match = mapEntry.toPair()
        }
        return match
    }

    private fun getPlayer() =
        GameInstance.getClient().player ?: throw RuntimeException("There really ought to be a player here")

    // Vanilla Box#contains method has some weird rounding issues, doesn't do what I'm looking for
    private fun Box.contains(player: PlayerEntity): Boolean {
        fun adjustPos(pos: Vec3d) = Vec3d(floor(pos.x), pos.y, floor(pos.z))
        fun withinDim(pos: Double, min: Double, max: Double) = pos in min..max

        val pos = adjustPos(player.pos)
        return withinDim(pos.x, minX, maxX) && withinDim(pos.y, minY, maxY) && withinDim(pos.z, minZ, maxZ)
    }

    private class Swapper<T> {
        var current: T? = null
            private set
        private var pending: T? = null

        fun submit(submission: T?) {
            if (current == null) assert(pending == null)
            when (current) {
                null -> {
                    current = submission
                    pending = submission
                }
                else -> pending = submission
            }
            SoundBounds.LOGGER.warn("SWAPPER: submitted $submission")
        }

        fun push() {
            current = pending
            SoundBounds.LOGGER.warn("SWAPPER: pushed $current to current")
        }
    }

    data class PlayerData(
        var blockPos: BlockPos?, // current block position of player
        var region: RegionEntry? // region the player is currently standing in
    ) {
        fun updatePos() {
            blockPos = getPlayer().blockPos
        }
    }
}
