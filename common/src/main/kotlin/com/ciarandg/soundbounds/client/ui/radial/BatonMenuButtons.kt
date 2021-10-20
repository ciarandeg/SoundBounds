package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.SoundBounds
import net.minecraft.util.Identifier
import kotlin.math.PI

class BatonMenuButtons {
    private val buttons: List<RadialButton> = listOf(
        RadialFolder(
            listOf(
                object : RadialButton({}, ZERO, THIRD, commitSubtractiveHoverTexture) {},
                object : RadialButton({}, THIRD, THIRD + THIRD, commitHighlightHoverTexture) {},
                object : RadialButton({}, THIRD + THIRD, FULL, commitAdditiveHoverTexture) {}
            ),
            HALF + EIGHTH, FULL, commitHoverTexture
        ),
        RadialFolder(
            listOf(),
            ZERO, QUARTER + EIGHTH, selectionHoverTexture
        ),
        object : RadialButton({}, QUARTER + EIGHTH, HALF, redoHoverTexture) {},
        object : RadialButton({}, HALF, HALF + EIGHTH, undoHoverTexture) {}
    )

    fun getHoveredButton(mousePos: PolarCoordinate): RadialButton = buttons.first { it.isBisected(mousePos) }

    companion object {
        private val commitHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_hover.png")
        private val selectionHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_hover.png")
        private val undoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_hover.png")
        private val redoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_hover.png")

        private val commitAdditiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_additive_hover.png")
        private val commitSubtractiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_subtractive_hover.png")
        private val commitHighlightHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_highlight_hover.png")

        const val ZERO = 0.0
        const val EIGHTH = PI * 0.25
        const val QUARTER = PI * 0.5
        const val THIRD = PI * 2.0 / 3.0
        const val HALF = PI
        const val FULL = PI * 2.0
    }
}
