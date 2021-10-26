package com.ciarandg.soundbounds.client.ui.baton.modes.commit

import com.ciarandg.soundbounds.SoundBounds
import com.ciarandg.soundbounds.client.render.RenderColor
import net.minecraft.util.Identifier

enum class CommitMode(val texture: Identifier, val wireframeColor: RenderColor) {
    ADDITIVE(Identifier(SoundBounds.MOD_ID, "textures/entity/additive_selection.png"), RenderColor(64, 78, 160)),
    SUBTRACTIVE(Identifier(SoundBounds.MOD_ID, "textures/entity/subtractive_selection.png"), RenderColor(160, 64, 72))
}
