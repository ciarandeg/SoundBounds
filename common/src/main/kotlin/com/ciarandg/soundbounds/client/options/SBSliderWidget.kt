package com.ciarandg.soundbounds.client.options

import net.minecraft.client.gui.widget.OptionSliderWidget
import net.minecraft.text.LiteralText

class SBSliderWidget(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    initial: Double,
    private val label: String,
    private val formatForDisplay: (Double) -> Any,
    private val onChange: (Double) -> Unit,
    active: Boolean = true
) : OptionSliderWidget(null, x, y, width, height, initial) {

    init {
        updateMessage()
        this.active = active
    }

    override fun updateMessage() {
        message = LiteralText("$label: ${formatForDisplay(value)}")
    }

    override fun applyValue() {
        onChange(value)
    }
}
