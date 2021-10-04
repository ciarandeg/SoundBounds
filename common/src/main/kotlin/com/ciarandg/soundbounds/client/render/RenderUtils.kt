package com.ciarandg.soundbounds.client.render

import com.ciarandg.soundbounds.client.render.RenderUtils.Companion.Z_INCREMENT
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box

class RenderUtils {
    companion object {
        const val Z_INCREMENT = 0.001 // used to prevent z-fighting
    }
}

fun BlockPos.toBox() = Box(
    this.x.toDouble(), this.y.toDouble(), this.z.toDouble(),
    this.x.toDouble() + 1.0, this.y.toDouble() + 1.0, this.z.toDouble() + 1.0
).expand(Z_INCREMENT)
