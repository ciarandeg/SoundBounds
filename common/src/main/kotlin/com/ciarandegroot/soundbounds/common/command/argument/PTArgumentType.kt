package com.ciarandegroot.soundbounds.common.command.argument

import com.ciarandegroot.soundbounds.common.util.PlaylistType
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import java.security.InvalidParameterException

class PTArgumentType : ArgumentType<PlaylistType> {
    override fun parse(reader: StringReader?): PlaylistType {
        val arg: String = reader?.readString() ?: ""
        for (p in PlaylistType.values()) {
            if (arg == p.name) return p
        }
        throw InvalidParameterException("\"$arg\" is not a Playlist type")
    }

    companion object {
        fun type(): PTArgumentType = PTArgumentType()

        fun getPlaylistType(ctx: CommandContext<ServerCommandSource>, name: String): PlaylistType =
            ctx.getArgument(name, PlaylistType.SEQUENTIAL.javaClass)
    }
}
