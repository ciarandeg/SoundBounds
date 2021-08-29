package com.ciarandg.soundbounds.client.options

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.OptionButtonWidget
import net.minecraft.client.options.BooleanOption
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW
import kotlin.math.pow

class SBOptionsScreen : Screen(LiteralText("SoundBounds Client Options")) {
    override fun init() {
        val xCenter = width / 2
        val widgetWidth = 150
        val widgetHeight = 20
        val spacer = widgetHeight / 4
        val top = height / 4 - widgetHeight / 2 + spacer

        val xPos = xCenter - widgetWidth / 2
        var counter = 0
        fun nextY() = top + (widgetHeight + spacer) * counter++

        super.init()
        addButton(
            ButtonWidget(
                xPos + widgetWidth + spacer, top,
                widgetHeight, widgetHeight,
                LiteralText("R")
            ) {
                SBClientOptions.setToDefault()
                client?.openScreen(SBOptionsScreen())
            }
        )
        addButton(
            SBSliderWidget(
                xPos, nextY(),
                widgetWidth, widgetHeight,
                0.0,
                "Fade Duration",
                { "ms" },
                { value -> (value * 100).toInt() },
                {}, false
            )
        )
        addButton(
            SBSliderWidget(
                xPos, nextY(),
                widgetWidth, widgetHeight,
                fromIdleDur(),
                "Post-Song Idle",
                { value -> formatDuration(toIdleDur(value), { "ms" }, { "secs" }, { "mins" }) },
                { value -> formatDuration(toIdleDur(value), { it.toString() }, { truncate(it) }, { truncate(it) }) },
                { value -> SBClientOptions.data.idleDuration = toIdleDur(value) },
            )
        )
        addButton(
            SBSliderWidget(
                xPos, nextY(),
                widgetWidth, widgetHeight,
                fromBufDur(),
                "Buffer Size",
                { "ms" },
                { value -> toBufDur(value) },
                { value -> SBClientOptions.data.bufferDuration = toBufDur(value) }
            )
        )
        addButton(
            SBSliderWidget(
                xPos, nextY(),
                widgetWidth, widgetHeight,
                fromLookahead(),
                "Lookahead",
                { "buffers" },
                { value -> toLookahead(value) },
                { value -> SBClientOptions.data.lookahead = toLookahead(value) }
            )
        )
        val diskStreamButton = OptionButtonWidget(
            xPos, nextY(),
            widgetWidth, widgetHeight,
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
                xPos, nextY(),
                widgetWidth, widgetHeight,
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
            textRenderer, title, width / 2, 30, 0xffffff
        )
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun onClose() {
        SBClientOptions.write()
        super.onClose()
    }

    companion object {
        private const val IDLE_SLIDER_SKEW: Double = 2.0
        val binding = KeyBinding("Client Options Screen", GLFW.GLFW_KEY_B, "SoundBounds")

        private fun formatDuration(
            milliseconds: Long,
            ms: (Long) -> String,
            secs: (Double) -> String,
            mins: (Double) -> String
        ): String = when {
            milliseconds < 1000 -> ms(milliseconds)
            milliseconds < 60 * 1000 -> secs(milliseconds.toDouble() / 1000.0)
            else -> mins(milliseconds.toDouble() / 1000.0 / 60.0)
        }
        private fun truncate(value: Double) = String.format("%.2f", value)

        private fun toIdleDur(sliderValue: Double): Long {
            val range: Long = SBClientOptions.MAX_IDLE_DUR - SBClientOptions.MIN_IDLE_DUR
            val skewed: Double = range * sliderValue.pow(IDLE_SLIDER_SKEW)
            val exact: Long = skewed.toLong() + SBClientOptions.MIN_IDLE_DUR
            val out = exact - exact % SBClientOptions.IDLE_DUR_STEP
            return out
        }

        private fun fromIdleDur(): Double {
            val min: Long = SBClientOptions.MIN_IDLE_DUR
            val range: Double = (SBClientOptions.MAX_IDLE_DUR - min).toDouble()
            val pctLinear: Double = (SBClientOptions.data.idleDuration - min) / range
            return pctLinear.pow(1.0 / IDLE_SLIDER_SKEW)
        }

        private fun toBufDur(sliderValue: Double): Long {
            val range: Long = SBClientOptions.MAX_BUF_DUR - SBClientOptions.MIN_BUF_DUR
            return (sliderValue * range).toLong() + SBClientOptions.MIN_BUF_DUR
        }

        private fun fromBufDur(): Double {
            val min: Long = SBClientOptions.MIN_BUF_DUR
            val range: Double = (SBClientOptions.MAX_BUF_DUR - min).toDouble()
            return (SBClientOptions.data.bufferDuration - min) / range
        }

        private fun toLookahead(sliderValue: Double): Int {
            val range: Int = SBClientOptions.MAX_LOOKAHEAD - SBClientOptions.MIN_LOOKAHEAD
            return (sliderValue * range).toInt() + SBClientOptions.MIN_LOOKAHEAD
        }

        private fun fromLookahead(): Double {
            val min: Int = SBClientOptions.MIN_LOOKAHEAD
            val range: Double = (SBClientOptions.MAX_LOOKAHEAD - min).toDouble()
            return (SBClientOptions.data.lookahead - min) / range
        }

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
