package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.*
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.repository.variableTypeId
import com.rodev.jbpkmp.presentation.screens.editor_screen.GlobalVariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.VariableNode
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.model.Node

class VariableNodeRepresentation(
    private val selectionHandler: SelectionHandler,
    private val variableState: VariableState,
    val variableId: String
) : NodeRepresentation {

    private var selected: Boolean by mutableStateOf(false)

    private val subHeader by derivedStateOf {
        return@derivedStateOf if (variableState is GlobalVariableState) {
            when (variableState.type) {
                GlobalVariable.Type.SAVED -> "Сохранённая переменная"
                GlobalVariable.Type.GAME -> "Игровая переменная"
            }
        } else {
            "Локальная переменная"
        }
    }

    @Composable
    override fun onDraw(
        nodeState: NodeState,
        pinDragListener: PinDragListener,
        snapshotRequester: SnapshotRequester
    ) {
        VariableNode(
            nodeState = nodeState,
            header = variableState.name,
            subHeader = subHeader,
            pinDragListener,
            snapshotRequester,
            selected,
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
            typeId = variableTypeId,
            inputPins = emptyList(),
            outputPins = nodeState.outputPins.map { it.pinState }.map { it.pinRepresentation.toPin(it) }
        )
    }

}