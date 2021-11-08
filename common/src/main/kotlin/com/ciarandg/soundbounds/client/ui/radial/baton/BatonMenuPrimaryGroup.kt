package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.GreyableRadialButton
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.EIGHTH
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.FULL
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.HALF
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.QUARTER
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.ZERO
import com.ciarandg.soundbounds.client.ui.radial.RadialFolder
import net.minecraft.util.Identifier

class BatonMenuPrimaryGroup : MenuButtonGroup(
    listOf(
        RadialFolder({ BatonMenuSecondaryCommitGroup() }, HALF.rad + EIGHTH.rad, FULL.rad, commitDefaultTexture, commitHoverTexture),
        RadialFolder({ BatonMenuSecondarySelectionGroup() }, ZERO.rad, QUARTER.rad + EIGHTH.rad, selectionDefaultTexture, selectionHoverTexture),
        GreyableRadialButton({ ClientSelectionController.redo() }, { !ClientSelectionController.canRedo() }, QUARTER.rad + EIGHTH.rad, HALF.rad, redoDefaultTexture, redoHoverTexture, redoGreyedTexture),
        GreyableRadialButton({ ClientSelectionController.undo() }, { !ClientSelectionController.canUndo() }, HALF.rad, HALF.rad + EIGHTH.rad, undoDefaultTexture, undoHoverTexture, undoGreyedTexture)
    )
) {
    companion object {
        private val commitDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_default.png")
        private val commitHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_hover.png")
        private val selectionDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_default.png")
        private val selectionHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_hover.png")
        private val undoDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_default.png")
        private val undoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_hover.png")
        private val undoGreyedTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_greyed.png")
        private val redoDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_default.png")
        private val redoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_hover.png")
        private val redoGreyedTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_greyed.png")
    }
}
