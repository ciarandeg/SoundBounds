package com.ciarandg.soundbounds.client.ui.radial

import net.minecraft.util.Identifier

open class RadialFolder(
    getSubGroup: () -> MenuButtonGroup,
    startAngle: Double,
    endAngle: Double,
    hoverTexture: Identifier
) : RadialButton(getSubGroup, startAngle, endAngle, hoverTexture)
