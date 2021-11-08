package com.ciarandg.soundbounds.client.ui.baton.selection

import com.ciarandg.soundbounds.client.render.RegionVisualizationRenderer
import com.ciarandg.soundbounds.client.render.RenderColor
import com.ciarandg.soundbounds.client.ui.baton.BatonMarker
import com.ciarandg.soundbounds.client.ui.baton.modes.commit.CommitMode
import com.ciarandg.soundbounds.client.ui.baton.modes.selection.AbstractSelectionMode
import com.ciarandg.soundbounds.client.ui.commands.AddToSelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.ClearSelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.ISelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.MacroSelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.RemoveFromSelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.SetBatonMarkerSelectionCommand
import com.ciarandg.soundbounds.client.ui.commands.SetBatonSelectionModeCommand
import com.ciarandg.soundbounds.client.ui.commands.SetCommitModeCommand
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import java.util.Stack

object ClientSelectionController {
    private val model = ClientSelectionModel()
    private val past = Stack<ISelectionCommand>()
    private val future = Stack<ISelectionCommand>()

    private const val markerCooldown = 1000

    fun undo() {
        val previous = past.pop()
        previous.unexecute()
        future.push(previous)
    }
    fun redo() {
        val subsequent = future.pop()
        subsequent.execute()
        past.push(subsequent)
    }
    fun canUndo() = past.isNotEmpty()
    fun canRedo() = future.isNotEmpty()

    fun renderUncommitted(matrixStack: MatrixStack) = model.uncommittedSelection.render(matrixStack, model.batonState.commitMode.texture, model.batonState.commitMode.wireframeColor)
    fun renderCommitted(matrixStack: MatrixStack) = model.committedSelection.render(matrixStack, RegionVisualizationRenderer.committedHighlightTexture, RenderColor(64, 160, 85))
    fun renderCursor(matrixStack: MatrixStack) = with(model.batonState) {
        cursor.render(matrixStack)
        selectionMode.renderMarkers(matrixStack)
    }

    fun setCursorPosition(player: PlayerEntity, tickDelta: Float) =
        model.batonState.cursor.setMarkerFromRaycast(player, tickDelta)
    fun getCursorMarker() =
        model.batonState.cursor.getMarker()
    fun bindCursor(player: PlayerEntity, tickDelta: Float) =
        model.batonState.cursor.bindToCurrentRadius(player, tickDelta)
    fun unbindCursor() = model.batonState.cursor.unbind()
    fun isCursorBounded() = model.batonState.cursor.isBounded()
    fun incrementCursorRadius(delta: Double) = with(model.batonState.cursor) {
        incrementRadius(delta)
    }

    private fun setBatonMarker(batonMarker: BatonMarker) {
        val newPos = model.batonState.cursor.getMarker()?.getPos() ?: return
        val oldPos = with(model.batonState.selectionMode) {
            when (batonMarker) {
                BatonMarker.FIRST -> getFirstMarkerPos()
                BatonMarker.SECOND -> getSecondMarkerPos()
            }
        }
        val time = System.currentTimeMillis()

        val isCoolingDown = time - batonMarker.timestamp < markerCooldown
        val isNewBlock = newPos != oldPos
        if (!isCoolingDown || isNewBlock) {
            batonMarker.timestamp = time
            executeCommand(
                SetBatonMarkerSelectionCommand(
                    batonMarker, newPos, model.batonState, model.uncommittedSelection.blockTree
                )
            )
        }
    }
    fun setFirstBatonMarker() = setBatonMarker(BatonMarker.FIRST)
    fun setSecondBatonMarker() = setBatonMarker(BatonMarker.SECOND)

    private fun executeCommand(command: ISelectionCommand) {
        command.execute()
        past.push(command)
        future.clear()
    }

    fun commit() = executeCommand(
        when (model.batonState.commitMode) {
            CommitMode.ADDITIVE -> AddToSelectionCommand(model.committedSelection.blockTree, model.uncommittedSelection.blockTree)
            CommitMode.SUBTRACTIVE -> RemoveFromSelectionCommand(model.committedSelection.blockTree, model.uncommittedSelection.blockTree)
        }
    )
    fun setCommitMode(mode: CommitMode) =
        executeCommand(SetCommitModeCommand(mode, model.batonState))
    fun getCommitMode() = model.batonState.commitMode
    fun setBatonSelectionMode(mode: AbstractSelectionMode) =
        executeCommand(SetBatonSelectionModeCommand(mode, model.batonState))
    fun addToUncommitted(blockSet: Set<BlockPos>) =
        executeCommand(AddToSelectionCommand(model.uncommittedSelection.blockTree, blockSet))
    fun removeFromUncommitted(blockSet: Set<BlockPos>) =
        executeCommand(RemoveFromSelectionCommand(model.uncommittedSelection.blockTree, blockSet))
    fun clearUncommitted() =
        executeCommand(ClearSelectionCommand(model.uncommittedSelection.blockTree))
    fun setUncommitted(blockSet: Set<BlockPos>) =
        executeCommand(object : MacroSelectionCommand(
            listOf(
                lazy { ClearSelectionCommand(model.uncommittedSelection.blockTree) },
                lazy { AddToSelectionCommand(model.uncommittedSelection.blockTree, blockSet) }
            )
        ) {})
    fun getUncommitted() = model.uncommittedSelection
    fun addToCommitted(blockSet: Set<BlockPos>) =
        executeCommand(AddToSelectionCommand(model.committedSelection.blockTree, blockSet))
    fun removeFromCommitted(blockSet: Set<BlockPos>) =
        executeCommand(RemoveFromSelectionCommand(model.committedSelection.blockTree, blockSet))
    fun clearCommitted() =
        executeCommand(ClearSelectionCommand(model.committedSelection.blockTree))
    fun setCommitted(blockSet: Set<BlockPos>) =
        executeCommand(object : MacroSelectionCommand(
            listOf(
                lazy { ClearSelectionCommand(model.committedSelection.blockTree) },
                lazy { AddToSelectionCommand(model.committedSelection.blockTree, blockSet) }
            )
        ) {})
    fun getCommitted() = model.committedSelection
}
