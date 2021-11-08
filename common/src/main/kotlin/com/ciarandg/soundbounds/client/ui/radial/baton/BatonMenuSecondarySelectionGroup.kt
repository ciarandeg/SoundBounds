package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.AbstractSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.BoxHighlightSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.BoxSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.ExtruderSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.MoveSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.SIXTH
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import net.minecraft.util.Identifier

class BatonMenuSecondarySelectionGroup : MenuButtonGroup(buildButtons()) {
    companion object {
        private val boxDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_default.png")
        private val boxHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_hover.png")
        private val sphereDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_sphere_default.png")
        private val sphereHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_sphere_hover.png")
        private val extruderDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_extruder_default.png")
        private val extruderHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_extruder_hover.png")
        private val moveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_move_default.png")
        private val moveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_move_hover.png")
        private val boxHighlightDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_highlight_default.png")
        private val boxHighlightHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_highlight_hover.png")
        private val wandDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_wand_highlight_default.png")
        private val wandHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_wand_highlight_hover.png")

        private fun buildButtons(): List<SelectionModeButton> {
            val increment = SIXTH.rad
            return listOf(
                Triple({ BoxSelectionMode() }, boxDefaultTexture, boxHoverTexture),
                Triple({ BoxSelectionMode() }, sphereDefaultTexture, sphereHoverTexture),
                Triple({ ExtruderSelectionMode() }, extruderDefaultTexture, extruderHoverTexture),
                Triple({ MoveSelectionMode() }, moveDefaultTexture, moveHoverTexture),
                Triple({ BoxHighlightSelectionMode() }, boxHighlightDefaultTexture, boxHighlightHoverTexture),
                Triple({ BoxSelectionMode() }, wandDefaultTexture, wandHoverTexture)
            ).mapIndexed { index, data ->
                SelectionModeButton(data.first, increment * index, increment * (index + 1), data.second, data.third)
            }
        }
    }

    private class SelectionModeButton(getMode: () -> AbstractSelectionMode, startAngle: Double, endAngle: Double, defaultTexture: Identifier, hoverTexture: Identifier) : RadialButton(
        { ClientSelectionController.setBatonSelectionMode(getMode()) },
        startAngle, endAngle, defaultTexture, hoverTexture
    )
}
