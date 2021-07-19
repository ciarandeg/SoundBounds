package com.ciarandg.soundbounds.common.ui.cli.command

import com.ciarandg.soundbounds.common.ui.cli.Assembler
import com.ciarandg.soundbounds.common.ui.cli.command.nodes.RootNode
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource as Source

object SoundBoundsCommand {
    fun register(dispatcher: CommandDispatcher<Source>) {
        dispatcher.register(Assembler.assembleLiteral(RootNode))
    }
}
