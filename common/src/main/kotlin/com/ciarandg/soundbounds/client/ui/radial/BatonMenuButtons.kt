package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.util.Identifier
import kotlin.math.PI

class BatonMenuButtons {
    private val buttons: List<RadialButton> = listOf(
        RadialFolder(listOf(), 0.0, ZERO, QUARTER, folderTexture1),
        RadialFolder(listOf(), 0.0, QUARTER, HALF, folderTexture2),
        RadialFolder(listOf(), 0.0, HALF, THREE_QUARTERS, folderTexture3),
        RadialFolder(listOf(), 0.0, THREE_QUARTERS, FULL, folderTexture4)
    )

    fun getHoveredButton(mousePos: PolarCoordinate): RadialButton {
        // if a folder is already open, give it precedence
        // iterate through all
        return buttons.first { it.isHovered(mousePos) }
    }

    companion object {
        private val folderTexture1 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_1.png")
        private val folderTexture2 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_2.png")
        private val folderTexture3 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_3.png")
        private val folderTexture4 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_4.png")

        const val ZERO = 0.0
        const val QUARTER = PI * 0.5
        const val HALF = PI
        const val THREE_QUARTERS = PI * 1.5
        const val FULL = PI * 2.0
    }
}
