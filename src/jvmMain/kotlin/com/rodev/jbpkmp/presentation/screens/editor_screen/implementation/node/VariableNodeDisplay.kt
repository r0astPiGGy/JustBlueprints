package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.presentation.screens.editor_screen.GlobalVariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.VariableNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.createVariableNodeTag
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node

class VariableNodeDisplay(
    private val selectionHandler: SelectionHandler,
    private val variableState: VariableState,
    val variableId: String
) : NodeDisplay {

    private var selected: Boolean by mutableStateOf(false)

    private val subHeader by derivedStateOf {
        variableState.type
    }

    @Composable
    override fun NodeView(
        nodeState: NodeState
    ) {
        VariableNode(
            nodeState = nodeState,
            header = variableState.name,
            subHeader = subHeader.typeName,
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
                nodeState = nodeState,
                detailsComposable = { variableState.Details() }
            )
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