package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.VariableNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.createVariableNodeTag
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node
import java.util.*

class VariableNodeDisplay(
    private val selectionHandler: SelectionHandler,
    variableState: VariableState,
    val variableId: String
) : NodeDisplay {

    private val state = VariableNodeState(variableState)

    @Composable
    override fun NodeView(
        nodeState: NodeState
    ) {
        VariableNode(
            nodeState = nodeState,
            state = state,
            onTap = { onSelect(nodeState) }
        )
    }

    private fun onSelect(nodeState: NodeState) {
        if (state.selected) {
            selectionHandler.resetSelection()
            return
        }
        selectionHandler.onSelect(
            NodeStateSelectableWrapper(
                selectGetter = { state.selected },
                selectSetter = { state.selected = it },
                nodeState = nodeState,
                nodeSupplier = { copyToNode(nodeState) },
                detailsComposable = { state.Details() }
            )
        )
    }

    private fun copyToNode(nodeState: NodeState): Node {
        return Node(
            x = nodeState.x,
            y = nodeState.y,
            uniqueId = UUID.randomUUID().toString(),
            inputPins = emptyList(),
            outputPins = nodeState.outputPins.map { it.pinState }.map {
                it.pinDisplay
                    .toPin(it)
                    .copy(uniqueId = UUID.randomUUID().toString())
            },
            tag = createVariableNodeTag(variableId)
        )
    }

    override fun toNode(nodeState: NodeState): Node {
        return Node(
            x = nodeState.x,
            y = nodeState.y,
            uniqueId = nodeState.id,
            inputPins = emptyList(),
            outputPins = nodeState.outputPins.map { it.pinState }.map { it.pinDisplay.toPin(it) },
            tag = createVariableNodeTag(variableId)
        )
    }
}

class VariableNodeState(
    private val variableState: VariableState
) {

    var selected by mutableStateOf(false)
    val name by derivedStateOf {
        variableState.name
    }
    val subHeader by derivedStateOf {
        variableState.type
    }

    @Composable
    fun Details() {
        variableState.Details()
    }

}