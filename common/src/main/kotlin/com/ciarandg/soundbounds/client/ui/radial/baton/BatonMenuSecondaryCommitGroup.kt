package com.ciarandg.soundbounds.client.ui.radial.baton

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.ui.ClientPlayerModel
import com.ciarandg.soundbounds.client.ui.baton.CommitMode
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.FULL
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.THIRD
import com.ciarandg.soundbounds.client.ui.radial.MenuButtonGroup.Angles.ZERO
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import net.minecraft.util.Identifier

class BatonMenuSecondaryCommitGroup : MenuButtonGroup(
    listOf(
        object : RadialButton({ changeCommitMode(CommitMode.ADDITIVE) }, THIRD.rad + THIRD.rad, FULL.rad, additiveHoverTexture) {},
        object : RadialButton({ changeCommitMode(CommitMode.SUBTRACTIVE) }, ZERO.rad, THIRD.rad, subtractiveHoverTexture) {},
        object : RadialButton({}, THIRD.rad, THIRD.rad + THIRD.rad, highlightHoverTexture) {}
    )
) {
    companion object {
        private val additiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_additive_hover.png")
        private val subtractiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_subtractive_hover.png")
        private val highlightHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_commit_highlight_hover.png")

        private fun changeCommitMode(mode: CommitMode) {
            ClientPlayerModel.batonState.commitMode = mode
        }
    }
}