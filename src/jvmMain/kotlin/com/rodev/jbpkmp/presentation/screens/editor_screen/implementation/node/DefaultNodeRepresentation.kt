package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.EditorScreenViewModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.Selectable
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.SimpleNode
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.model.Node

class DefaultNodeRepresentation(
    private val nodeEntity: NodeEntity,
    private val selectionHandler: SelectionHandler
) : NodeRepresentation {

    private var selected: Boolean by mutableStateOf(false)

    @Composable
    override fun onDraw(nodeState: NodeState, pinDragListener: PinDragListener, snapshotRequester: SnapshotRequester) {
        SimpleNode(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            pinDragListener = pinDragListener,
            snapshotRequester = snapshotRequester,
            selected = selected,
            onTap = { onSelect(nodeState) }
        )
    }

    private fun onSelect(nodeState: NodeState) {
        if (selected) {
            selectionHandler.resetSelection()
            return
        }
        selectionHandler.onSelect(
            NodeStateSelectableWrapper(
                selectGetter = { selected },
                selectSetter = { selected = it },
                nodeState = nodeState
            )
        )
    }

    override fun toNode(nodeState: NodeState): Node {
        return Node(
            x = nodeState.x,
            y = nodeState.y,
            uniqueId = nodeState.id,
            typeId = nodeEntity.id,
            inputPins = nodeState.inputPins.map { it.pinState }.map { it.pinRepresentation.toPin(it) },
            outputPins = nodeState.outputPins.map { it.pinState }.map { it.pinRepresentation.toPin(it) }
        )
    }

}

private class NodeStateSelectableWrapper(
    private val selectSetter: (Boolean) -> Unit,
    private val selectGetter: () -> Boolean,
    private val nodeState: NodeState
) : Selectable {

    override var selected: Boolean
        get() = selectGetter()
        set(value) {
            selectSetter(value)
        }

    override fun onDelete(editorScreenViewModel: EditorScreenViewModel) {
        editorScreenViewModel.currentGraph?.viewModel?.deleteNode(nodeState)
    }

}