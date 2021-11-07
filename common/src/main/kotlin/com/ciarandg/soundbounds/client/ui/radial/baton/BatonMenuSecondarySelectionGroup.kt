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
        private val boxHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_hover.png")
        private val sphereHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_sphere_hover.png")
        private val extruderHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_extruder_hover.png")
        private val moveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_move_hover.png")
        private val boxHighlightHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_box_highlight_hover.png")
        private val wandHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_wand_highlight_hover.png")

        private fun buildButtons(): List<SelectionModeButton> {
            val increment = SIXTH.rad
            return listOf(
                Pair({ BoxSelectionMode() }, boxHoverTexture),
                Pair({ BoxSelectionMode() }, sphereHoverTexture),
                Pair({ ExtruderSelectionMode() }, extruderHoverTexture),
                Pair({ MoveSelectionMode() }, moveHoverTexture),
                Pair({ BoxHighlightSelectionMode() }, boxHighlightHoverTexture),
                Pair({ BoxSelectionMode() }, wandHoverTexture)
            ).mapIndexed { index, data ->
                SelectionModeButton(data.first, increment * index, increment * (index + 1), data.second)
            }
        }
    }

    private class SelectionModeButton(getMode: () -> AbstractSelectionMode, startAngle: Double, endAngle: Double, hoverTexture: Identifier) : RadialButton(
        { ClientSelectionController.setBatonSelectionMode(getMode()) },
        startAngle, endAngle, hoverTexture
    )
}
