package com.ciarandg.soundbounds.client.render

import kotlin.math.max
import kotlin.math.min

data class RenderColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = CEIL
) {
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(
        red.toFloat() / 255.0f,
        green.toFloat() / 255.0f,
        blue.toFloat() / 255.0f,
        alpha.toFloat() / 255.0f
    )

    operator fun plus(c: RenderColor) = RenderColor(
        min(red + c.red, CEIL),
        min(green + c.green, CEIL),
        min(blue + c.blue, CEIL),
        min(alpha + c.alpha, CEIL)
    )

    operator fun minus(c: RenderColor) = RenderColor(
        max(red - c.red, FLOOR),
        max(green - c.green, FLOOR),
        max(blue - c.blue, FLOOR),
        max(alpha - c.alpha, FLOOR)
    )

    companion object {
        private const val CEIL = 1.0f
        private const val FLOOR = 0.0f

        val BLACK = RenderColor(FLOOR, FLOOR, FLOOR)
        val WHITE = RenderColor(CEIL, CEIL, CEIL)
        val RED = RenderColor(CEIL, FLOOR, FLOOR)
        val GREEN = RenderColor(FLOOR, CEIL, FLOOR)
        val BLUE = RenderColor(FLOOR, FLOOR, CEIL)
        val YELLOW = RED + GREEN
        val MAGENTA = RED + BLUE
        val CYAN = GREEN + BLUE
    }
}
