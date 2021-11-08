package com.ciarandg.soundbounds.client.ui.commands

internal abstract class MacroSelectionCommand(private val children: List<Lazy<ISelectionCommand>>) : ISelectionCommand {
    override fun execute() {
        children.forEach { it.value.execute() }
    }

    override fun unexecute() {
        children.reversed().forEach { it.value.unexecute() }
    }
}
