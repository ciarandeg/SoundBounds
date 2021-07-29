package com.ciarandg.soundbounds.common.ui.cli.command

import com.ciarandg.soundbounds.common.ui.cli.Assembler
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.RootNode
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource as Source

object SoundBoundsCommand {
    fun register(dispatcher: CommandDispatcher<Source>) {
        dispatcher.register(Assembler.assembleLiteral(RootNode).requires { it.hasPermissionLevel(4) })
        dispatcher.register(
            LiteralArgumentBuilder.literal<Source>("now-playing")
                .executes { cmd ->
                    dispatcher.execute("sb now-playing", cmd.source)
                    0
                }
        )
    }
}
