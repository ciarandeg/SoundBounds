package com.ciarandg.soundbounds.client.ui.radial.baton.buttons

import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import com.ciarandg.soundbounds.client.ui.baton.selection.ClientSelectionController
import com.ciarandg.soundbounds.client.ui.radial.RadialButton
import net.minecraft.util.Identifier

class ClearUncommittedButton(
    startAngle: Double,
    endAngle: Double,
    additiveDefaultTexture: Identifier,
    additiveHoverTexture: Identifier,
    subtractiveDefaultTexture: Identifier,
    subtractiveHoverTexture: Identifier
) : RadialButton(
    { ClientSelectionController.clearUncommitted() }, startAngle, endAngle,
    when (ClientSelectionController.getCommitMode()) {
        CommitMode.ADDITIVE -> additiveDefaultTexture
        CommitMode.SUBTRACTIVE -> subtractiveDefaultTexture
    },
    when (ClientSelectionController.getCommitMode()) {
        CommitMode.ADDITIVE -> additiveHoverTexture
        CommitMode.SUBTRACTIVE -> subtractiveHoverTexture
    }
)
