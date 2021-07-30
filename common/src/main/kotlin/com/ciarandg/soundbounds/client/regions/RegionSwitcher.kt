package com.ciarandg.soundbounds.client.regions

import com.ciarandg.soundbounds.ClientRegionEntry
import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.Fader
import com.ciarandg.soundbounds.client.exceptions.EmptyPlaylistException
import com.ciarandg.soundbounds.client.exceptions.MissingAudioException
import me.shedaniel.architectury.utils.GameInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.floor

object RegionSwitcher {
    // used to prevent unnecessary iteration
    private val playerData = PlayerData(null, null)

    // determines which region the player is hearing music from
    private val swapper = Swapper<ClientRegionEntry> { old, new ->
        fun handleException(e: Exception) = SoundBounds.LOGGER.warn(
            when (e) {
                is EmptyPlaylistException -> "Attempted to play music in region ${new?.first}, but playlist is empty"
                is MissingAudioException -> "Missing audio file: ${e.fileName}"
                else -> throw e
            }
        )
        if (old != new) {
            try { old?.second?.player?.stop() } catch (e: Exception) { handleException(e) }
            try { new?.second?.player?.start() } catch (e: Exception) { handleException(e) }
        }
    }
    val fader = initFader()

    private fun initFader(): Fader = Fader {
        swapper.push()
        fader.reset()
    }

    fun update() {
        swapper.current?.second?.player?.tick()

        val oldPos = playerData.blockPos
        try {
            playerData.updatePos()
        } catch (e: RuntimeException) {
            SoundBounds.LOGGER.error("Attempted to update player position for nonexistent player")
            return
        }
        if (playerData.blockPos == oldPos) return
        updateRegion()
    }

    fun purge() {
        swapper.submit(null)
        swapper.push()
        playerData.blockPos = null
        playerData.region = null
    }

    fun currentSongID() = swapper.current?.second?.player?.currentSongID()
    fun currentRegionName() = swapper.current?.first

    private fun updateRegion() {
        val newRegion = searchPlayerRegion()
        if (newRegion != playerData.region) {
            playerData.region = newRegion
            swapper.submit(newRegion)
            if (newRegion != swapper.current) fader.requestFade()
            else fader.reset()
        }
    }

    private fun searchPlayerRegion(): ClientRegionEntry? {
        val player = getPlayer()
        var match: ClientRegionEntry? = null
        ClientWorldRegions.entries.forEach { mapEntry ->
            val containsPlayer = mapEntry.value.data.volumes.any {
                Box(it.first, it.second).contains(player)
            }
            val previousBest = match?.second?.data?.priority
            val hasPriority = previousBest == null || mapEntry.value.data.priority > previousBest
            if (containsPlayer && hasPriority) match = mapEntry.toPair()
        }
        return match
    }

    private fun getPlayer() =
        GameInstance.getClient().player ?: throw RuntimeException("No client player present")

    // Vanilla Box#contains method has some weird rounding issues, doesn't do what I'm looking for
    private fun Box.contains(player: PlayerEntity): Boolean {
        fun adjustPos(pos: Vec3d) = Vec3d(floor(pos.x), pos.y, floor(pos.z))
        fun withinDim(pos: Double, min: Double, max: Double) = pos in min..max

        val pos = adjustPos(player.pos)
        return withinDim(pos.x, minX, maxX) && withinDim(pos.y, minY, maxY) && withinDim(pos.z, minZ, maxZ)
    }

    private class Swapper<T>(val postPush: (T?, T?) -> Unit) {
        var current: T? = null
            private set
        private var pending: T? = null

        fun submit(submission: T?) {
            pending = submission
            if (current == null) push()
        }

        fun push() {
            val old = current
            current = pending
            postPush(old, current)
        }
    }

    data class PlayerData(
        var blockPos: BlockPos?, // current block position of player
        var region: ClientRegionEntry? // region the player is currently standing in
    ) {
        fun updatePos() {
            blockPos = getPlayer().blockPos
        }
    }
}
