package com.ciarandg.soundbounds.server.ui.cli

import com.ciarandg.soundbounds.common.util.PlaylistType
import com.ciarandg.soundbounds.plus
import com.ciarandg.soundbounds.server.metadata.ServerMetaState
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

object Colors {
    val ERROR = Formatting.RED
    private val BODY = Formatting.GOLD
    private val REGION_PROPERTY = Formatting.DARK_PURPLE
    private val SONG_PROPERTY = Formatting.DARK_PURPLE
    private val URL = Formatting.BLUE
    private val GROUP_LINK = Formatting.GREEN
    private val POS_MARKER = Formatting.DARK_PURPLE
    private val BLOCK_POS = POS_MARKER

    fun bodyText(text: String) = formatText(text, BODY)
    fun regionNameText(text: String) = formatText(text, REGION_PROPERTY)
    fun playlistTypeText(type: PlaylistType) = formatText(type.toString(), REGION_PROPERTY)
    fun quantityText(quantity: Int) = formatText(quantity.toString(), BODY)
    fun richArtistText(artist: String, featuring: List<String>? = null): MutableText =
        if (featuring == null) singleArtistText(artist)
        else bodyText("").append(singleArtistText(artist)).append(bodyText(" ")).append(featuredArtistsText(featuring))
    fun plainArtistText(artist: String) = formatText(artist, SONG_PROPERTY)
    fun songIDText(title: String) = formatText(title, SONG_PROPERTY)
    fun songTitleText(title: String) = formatText(title, SONG_PROPERTY)
    fun songTagText(tag: String) = formatText(tag, SONG_PROPERTY)
    fun priorityText(priority: Int) = formatText(priority.toString(), REGION_PROPERTY)
    fun listPosText(pos: Int) = formatText(pos.toString(), BODY)
    fun posMarkerText(marker: PosMarker) = formatText(marker.toString(), POS_MARKER)
    fun blockPosText(pos: BlockPos) = formatText(pos.toString(), BLOCK_POS)
    fun volumeText(volume: Pair<BlockPos, BlockPos>) = formatText(volume.toString(), REGION_PROPERTY)
    fun songTagListText(tags: List<String>) = tags.mapIndexed { i, tag ->
        val tagText = songTagText(tag)
        if (i == tags.size - 1) tagText
        else tagText + bodyText(", ")
    }.fold(bodyText("")) { textIn, textOut -> textIn + textOut }

    private fun singleArtistText(artist: String): MutableText {
        val meta = ServerMetaState.get().meta
        val promo = meta.composers[artist]?.promo
        val groupMembers = meta.groups[artist]
        return when {
            promo != null -> formatText(artist, URL).fillStyle(
                Style.EMPTY
                    .withFormatting(Formatting.UNDERLINE)
                    .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, promo.toExternalForm()))
                    .withHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            LiteralText("Listen to more from ")
                                .append(LiteralText(artist).formatted(Formatting.ITALIC))
                                .append(LiteralText("!"))
                        )
                    )
            )
            groupMembers != null -> formatText(artist, GROUP_LINK).fillStyle(
                Style.EMPTY
                    .withFormatting(Formatting.UNDERLINE)
                    .withClickEvent(
                        ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/sb info group ${if (artist.contains(' ')) "\"$artist\"" else artist}"
                        )
                    ).withHoverEvent(
                        HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            LiteralText("View the members of ")
                                .append(LiteralText(artist).formatted(Formatting.ITALIC))
                        )
                    )
            )
            else -> formatText(artist, SONG_PROPERTY)
        }
    }
    private fun featuredArtistsText(featuring: List<String>): MutableText {
        val artists = featuring.map { singleArtistText(it) }
        val delimited = artists.mapIndexed { i, artist ->
            if (i < artists.size - 1) bodyText("").append(artist).append(bodyText(", "))
            else artist
        }
        val out = bodyText("feat. ")
        delimited.forEach { out.append(it) }
        return out
    }

    object Help {
        val COMMAND = Formatting.GOLD
        val DESCRIPTION = Formatting.BLUE
        val SEPARATOR = Formatting.GRAY
    }

    object ModBadge {
        private val BADGE = Formatting.DARK_AQUA
        fun formatModBadge(modName: String) = formatText("[$modName]", BADGE)
    }

    private fun formatText(text: String, format: Formatting): MutableText = LiteralText(text).formatted(format)
}
