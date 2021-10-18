package com.ciarandg.soundbounds.client.ui.radial

import com.ciarandg.soundbounds.client.ui.radial.buttons.RedoButton
import com.ciarandg.soundbounds.client.ui.radial.buttons.UndoButton

object BatonMenu {
    val commitModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    val selectionModeFolder = RadialFolder(listOf(), 0.0f, 0.0f)
    val undoButton = UndoButton(0.0f, 0.0f)
    val redoButton = RedoButton(0.0f, 0.0f)
}