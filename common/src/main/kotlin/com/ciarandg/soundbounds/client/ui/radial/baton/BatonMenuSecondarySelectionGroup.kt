package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.AbstractSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.BoxSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.ExtrudeSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.IntersectionSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.MoveSelectionMode
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.SIXTH
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import net.minecraft.util.Identifier

class BatonMenuSecondarySelectionGroup : MenuButtonGroup(buildButtons()) {
    companion object {
        private val boxDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_box_default.png")
        private val boxHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_box_hover.png")
        private val sphereDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_sphere_default.png")
        private val sphereHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_sphere_hover.png")
        private val extrudeDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_extrude_default.png")
        private val extrudeHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_extrude_hover.png")
        private val moveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_move_default.png")
        private val moveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_move_hover.png")
        private val intersectionDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_intersection_default.png")
        private val intersectionHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_intersection_hover.png")
        private val fillDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_fill_default.png")
        private val fillHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_selection_mode_fill_hover.png")

        private fun buildButtons(): List<SelectionModeButton> {
            val increment = SIXTH.rad
            return listOf(
                Triple({ BoxSelectionMode() }, boxDefaultTexture, boxHoverTexture),
                Triple({ BoxSelectionMode() }, sphereDefaultTexture, sphereHoverTexture),
                Triple({ ExtrudeSelectionMode() }, extrudeDefaultTexture, extrudeHoverTexture),
                Triple({ MoveSelectionMode() }, moveDefaultTexture, moveHoverTexture),
                Triple({ IntersectionSelectionMode() }, intersectionDefaultTexture, intersectionHoverTexture),
                Triple({ BoxSelectionMode() }, fillDefaultTexture, fillHoverTexture)
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
