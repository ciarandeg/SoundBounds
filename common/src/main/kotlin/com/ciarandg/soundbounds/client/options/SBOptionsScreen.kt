package com.ciarandg.soundbounds.client.options

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.OptionButtonWidget
import net.minecraft.client.options.BooleanOption
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class SBOptionsScreen : Screen(LiteralText("SoundBounds Options")) {
    override fun init() {
        val xCenter = width / 2
        val widgetWidth = 150
        val widgetHeight = 20
        val ySpacer = widgetHeight / 4
        val top = height / 4 - widgetHeight / 2 + ySpacer * 3

        super.init()
        addButton(
            SBSliderWidget(
                xCenter - widgetWidth / 2,
                top,
                widgetWidth,
                widgetHeight,
                0.0,
                "Fade Duration",
                { value -> (value * 100).toInt() },
                {}, false
            )
        )
        addButton(
            SBSliderWidget(
                xCenter - widgetWidth / 2,
                top + widgetHeight + ySpacer,
                widgetWidth,
                widgetHeight,
                0.0,
                "Buffer Size",
                { value -> (value * 100).toInt() },
                {}, false
            )
        )
        addButton(
            SBSliderWidget(
                xCenter - widgetWidth / 2,
                top + (widgetHeight + ySpacer) * 2,
                widgetWidth,
                widgetHeight,
                0.0,
                "Buffer Lookahead",
                { value -> (value * 100).toInt() },
                {}, false
            )
        )
        val diskStreamButton = OptionButtonWidget(
            xCenter - widgetWidth / 2,
            top + (widgetHeight + ySpacer) * 3,
            widgetWidth,
            widgetHeight,
            diskStreamOption,
            diskStreamOption.getDisplayString(null)
        ) { button: ButtonWidget ->
            diskStreamOption.toggle(client?.options)
            button.message = diskStreamOption.getDisplayString(client?.options)
            client?.options?.write()
        }
        diskStreamButton.active = false
        addButton(diskStreamButton)
        addButton(
            OptionButtonWidget(
                xCenter - widgetWidth / 2,
                top + (widgetHeight + ySpacer) * 4,
                widgetWidth,
                widgetHeight,
                autoNowPlayingOption,
                autoNowPlayingOption.getDisplayString(null)
            ) { button: ButtonWidget ->
                autoNowPlayingOption.toggle(client?.options)
                button.message = autoNowPlayingOption.getDisplayString(client?.options)
                client?.options?.write()
            }
        )
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        drawCenteredText(
            matrices,
            textRenderer, title, width / 2, 40, 0xffffff
        )
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun onClose() {
        SBClientOptions.write()
        super.onClose()
    }

    companion object {
        val diskStreamOption = BooleanOption(
            "Stream From Disk",
            { false },
            { _, _ -> }
        )

        val autoNowPlayingOption = BooleanOption(
            "Auto Now-Playing",
            { SBClientOptions.data.autoNowPlaying },
            { _, onOff -> SBClientOptions.data.autoNowPlaying = onOff }
        )
    }
}
