package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.FULL
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.HALF
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.ZERO
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import net.minecraft.util.Identifier

class BatonMenuSecondaryCommitGroup : MenuButtonGroup(
    listOf(
        object : RadialButton({ changeCommitMode(CommitMode.SUBTRACTIVE) }, ZERO.rad, HALF.rad, subtractiveHoverTexture) {},
        object : RadialButton({ changeCommitMode(CommitMode.ADDITIVE) }, HALF.rad, FULL.rad, additiveHoverTexture) {},
    )
) {
    companion object {
        private val additiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_additive_hover.png")
        private val subtractiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_subtractive_hover.png")

        private fun changeCommitMode(mode: CommitMode) = ClientSelectionController.setCommitMode(mode)
    }
}
