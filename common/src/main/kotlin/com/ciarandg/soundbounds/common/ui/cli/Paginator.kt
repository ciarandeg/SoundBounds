package com.ciarandg.soundbounds.common.ui.cli

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Style.EMPTY
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import kotlin.math.ceil

class Paginator {
    var state = PaginatorState()
    var style = PaginatorStyle()

    fun paginate(title: String, content: List<MutableText>): Text {
        val pageCount: Int = ceil(content.size.toDouble() / style.elementsPerPage).toInt()
        val boundedPageNumber = maxOf(1, minOf(state.page, pageCount))

        val out = generateTrim(LiteralText(" $title ").formatted(style.titleColor)).append("\n")

        val start = (boundedPageNumber - 1) * style.elementsPerPage
        val pageContent = content.subList(start, Integer.min(content.size, start + style.elementsPerPage))
        for (entry in pageContent) out.append(entry.append("\n"))

        return out.append(generateTrim(generateNavControls(boundedPageNumber, pageCount)))
    }

    private fun generateTrim(centeredText: MutableText = LiteralText("")): MutableText {
        val trim = TranslatableText("")
        val cStart: Int = style.trimLength / 2 - centeredText.string.length / 2
        val cEnd: Int = cStart + centeredText.string.length - 1
        for (i in 1..style.trimLength) {
            if (i == cStart) trim.append(centeredText)
            else if (i < cStart || i > cEnd) trim.append(
                LiteralText("${style.trimChar}").formatted(
                    style.trimColor
                )
            )
        }
        return trim
    }

    private fun generateNavControls(page: Int, pages: Int): MutableText {
        val prevPageButton = LiteralText("<")
        val nextPageButton = LiteralText(">")

        fun stylePageButton(active: Boolean, inc: Int, hoverText: String): Style {
            return if (active)
                EMPTY.withColor(style.activeNavColor)
                    .withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            state.commandFormat.replace(PAGE_DELIM, "${page + inc}")
                        )
                    ).withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, LiteralText(hoverText)))
            else EMPTY.withColor((style.inactiveNavColor))
        }

        prevPageButton.style = stylePageButton(page > 1, -1, "previous page")
        nextPageButton.style = stylePageButton(page < pages, 1, "next page")

        return TranslatableText(" ")
            .append(prevPageButton)
            .append(LiteralText(" [$page/$pages] ").formatted(style.pagerColor))
            .append(nextPageButton)
            .append(" ")
    }

    companion object {
        const val PAGE_DELIM: String = "%PAGE"
    }
}

data class PaginatorState(
    val commandFormat: String = "",
    val page: Int = 1,
)

data class PaginatorStyle(
    var elementsPerPage: Int = 8,
    var trimLength: Int = 48,
    var trimChar: Char = '=',
    var trimColor: Formatting = Formatting.DARK_AQUA,
    var titleColor: Formatting = Formatting.DARK_PURPLE,
    var activeNavColor: Formatting = Formatting.GOLD,
    var inactiveNavColor: Formatting = Formatting.DARK_GRAY,
    var pagerColor: Formatting = Formatting.GOLD
)
