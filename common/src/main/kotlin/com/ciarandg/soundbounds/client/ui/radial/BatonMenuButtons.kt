package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.util.Identifier
import kotlin.math.PI

class BatonMenuButtons {
    private val buttons: List<RadialButton> = listOf(
        RadialFolder(
            listOf(
                object : RadialButton({}, BUTTON_RADIUS, 1.0, ZERO, QUARTER - EIGHTH, subTexture1) {},
                object : RadialButton({}, BUTTON_RADIUS, 1.0, QUARTER - EIGHTH, QUARTER, subTexture2) {}
            ),
            0.0, BUTTON_RADIUS, ZERO, QUARTER, folderTexture1
        ),
        RadialFolder(
            listOf(
                object : RadialButton({}, BUTTON_RADIUS, 1.0, QUARTER, HALF - EIGHTH, subTexture3) {},
                object : RadialButton({}, BUTTON_RADIUS, 1.0, HALF - EIGHTH, HALF, subTexture4) {}
            ),
            0.0, BUTTON_RADIUS, QUARTER, HALF, folderTexture2
        ),
        RadialFolder(
            listOf(
                object : RadialButton({}, BUTTON_RADIUS, 1.0, HALF, THREE_QUARTERS - EIGHTH, subTexture5) {},
                object : RadialButton({}, BUTTON_RADIUS, 1.0, THREE_QUARTERS - EIGHTH, THREE_QUARTERS, subTexture6) {}
            ),
            0.0, BUTTON_RADIUS, HALF, THREE_QUARTERS, folderTexture3
        ),
        RadialFolder(
            listOf(
                object : RadialButton({}, BUTTON_RADIUS, 1.0, THREE_QUARTERS, FULL - EIGHTH, subTexture7) {},
                object : RadialButton({}, BUTTON_RADIUS, 1.0, FULL - EIGHTH, FULL, subTexture8) {}
            ),
            0.0, BUTTON_RADIUS, THREE_QUARTERS, FULL, folderTexture4
        )
    )
    private var openFolder: RadialFolder? = null

    fun getHoveredButton(mousePos: PolarCoordinate): RadialButton {
        val hoveredTopLevel =
            try { buttons.first { it.isHovered(mousePos) } } catch (e: NoSuchElementException) {
                openFolder ?: try { buttons.first { it.isBisected(mousePos) } } catch (e: NoSuchElementException) {
                    throw IllegalStateException("No usable button at mouse position $mousePos")
                }
            }
        openFolder = if (hoveredTopLevel is RadialFolder) hoveredTopLevel else null
        return hoveredTopLevel
    }

    companion object {
        private val folderTexture1 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_1.png")
        private val folderTexture2 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_2.png")
        private val folderTexture3 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_3.png")
        private val folderTexture4 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_unfold_4.png")
        private val subTexture1 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_1.png")
        private val subTexture2 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_2.png")
        private val subTexture3 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_3.png")
        private val subTexture4 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_4.png")
        private val subTexture5 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_5.png")
        private val subTexture6 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_6.png")
        private val subTexture7 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_7.png")
        private val subTexture8 = Identifier(SoundBounds.MOD_ID, "textures/radial/menu_sub_8.png")

        const val ZERO = 0.0
        const val EIGHTH = PI * 0.25
        const val QUARTER = PI * 0.5
        const val HALF = PI
        const val THREE_QUARTERS = PI * 1.5
        const val FULL = PI * 2.0

        const val BUTTON_RADIUS = 0.75
    }
}
