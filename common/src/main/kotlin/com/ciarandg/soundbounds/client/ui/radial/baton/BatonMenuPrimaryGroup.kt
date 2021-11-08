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
import com.ciarandg.soundbounds.client.ui.radial.baton.buttons.DoubleTextureButton
import net.minecraft.util.Identifier

class BatonMenuPrimaryGroup : MenuButtonGroup(
    listOf(
        RadialFolder({ BatonMenuSecondaryClearGroup() }, ZERO.rad, EIGHTH.rad, clearDefaultTexture, clearHoverTexture),
        RadialFolder({ BatonMenuSecondarySelectionGroup() }, EIGHTH.rad, QUARTER.rad + EIGHTH.rad, selectionModeDefaultTexture, selectionModeHoverTexture),
        GreyableRadialButton({ ClientSelectionController.redo() }, { !ClientSelectionController.canRedo() }, QUARTER.rad + EIGHTH.rad, HALF.rad, redoDefaultTexture, redoHoverTexture, redoGreyedTexture),
        GreyableRadialButton({ ClientSelectionController.undo() }, { !ClientSelectionController.canUndo() }, HALF.rad, HALF.rad + EIGHTH.rad, undoDefaultTexture, undoHoverTexture, undoGreyedTexture),
        RadialFolder({ BatonMenuSecondaryCommitGroup() }, HALF.rad + EIGHTH.rad, FULL.rad - EIGHTH.rad, commitModeDefaultTexture, commitModeHoverTexture),
        DoubleTextureButton({ ClientSelectionController.commit() }, FULL.rad - EIGHTH.rad, FULL.rad, commitAdditiveDefaultTexture, commitAdditiveHoverTexture, commitSubtractiveDefaultTexture, commitSubtractiveHoverTexture),
    )
) {
    companion object {
        private val clearDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_clear_default.png")
        private val clearHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_clear_hover.png")
        private val selectionModeDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_mode_default.png")
        private val selectionModeHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_selection_mode_hover.png")
        private val redoDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_default.png")
        private val redoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_hover.png")
        private val redoGreyedTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_redo_greyed.png")
        private val undoDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_default.png")
        private val undoHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_hover.png")
        private val undoGreyedTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_undo_greyed.png")
        private val commitModeDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_mode_default.png")
        private val commitModeHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_mode_hover.png")
        private val commitAdditiveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_additive_default.png")
        private val commitAdditiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_additive_hover.png")
        private val commitSubtractiveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_subtractive_default.png")
        private val commitSubtractiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/primary_commit_subtractive_hover.png")
    }
}
