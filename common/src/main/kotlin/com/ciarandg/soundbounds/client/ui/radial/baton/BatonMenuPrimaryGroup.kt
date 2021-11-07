package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.EIGHTH
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.FULL
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.HALF
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.QUARTER
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.ZERO
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import com.ciarandg.soundbounds.client.ui.radial.RadialFolder
import net.minecraft.util.Identifier

class BatonMenuPrimaryGroup : MenuButtonGroup(
    listOf(
        RadialFolder({ BatonMenuSecondaryCommitGroup() }, HALF.rad + EIGHTH.rad, FULL.rad, commitHoverTexture),
        RadialFolder({ BatonMenuSecondarySelectionGroup() }, ZERO.rad, QUARTER.rad + EIGHTH.rad, selectionHoverTexture),
        object : RadialButton({ ClientSelectionController.redo() }, QUARTER.rad + EIGHTH.rad, HALF.rad, redoHoverTexture) {},
        object : RadialButton({ ClientSelectionController.undo() }, HALF.rad, HALF.rad + EIGHTH.rad, undoHoverTexture) {}
    )
) {
    companion object {
        private val commitHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_hover.png")
        private val selectionHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_hover.png")
        private val undoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_hover.png")
        private val redoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_hover.png")
    }
}
