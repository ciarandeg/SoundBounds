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
import com.ciarandg.soundbounds.client.ui.radial.baton.buttons.ClearUncommittedButton
import net.minecraft.util.Identifier

class BatonMenuSecondaryClearGroup : MenuButtonGroup(
    listOf(
        object : RadialButton({ ClientSelectionController.clearCommitted() }, ZERO.rad, QUARTER.rad + EIGHTH.rad, committedDefaultTexture, committedHoverTexture) {},
        object : RadialButton(
            {
                ClientSelectionController.clearCommitted()
                ClientSelectionController.clearUncommitted()
            },
            QUARTER.rad + EIGHTH.rad, HALF.rad + EIGHTH.rad, bothDefaultTexture, bothHoverTexture
        ) {},
        ClearUncommittedButton(HALF.rad + EIGHTH.rad, FULL.rad, uncommittedAdditiveDefaultTexture, uncommittedAdditiveHoverTexture, uncommittedSubtractiveDefaultTexture, uncommittedSubtractiveHoverTexture)
    )
) {
    companion object {
        private val committedDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_committed_default.png")
        private val committedHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_committed_hover.png")
        private val uncommittedAdditiveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_uncommitted_additive_default.png")
        private val uncommittedAdditiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_uncommitted_additive_hover.png")
        private val uncommittedSubtractiveDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_uncommitted_subtractive_default.png")
        private val uncommittedSubtractiveHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_uncommitted_subtractive_hover.png")
        private val bothDefaultTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_both_default.png")
        private val bothHoverTexture = Identifier(SoundBounds.MOD_ID, "textures/radial/secondary_clear_both_hover.png")
    }
}
