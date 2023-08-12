package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ArrayNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.createNodeTypeTag
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.Node

class ArrayNodeDisplay(
    private val nodeEntity: NodeEntity,
    private val selectionHandler: SelectionHandler,
    private val actionDetails: ActionDetails?,
    private val arrayElementPinFactory: ArrayElementPinFactory
) : NodeDisplay {

    private var selected: Boolean by mutableStateOf(false)

    @Composable
    override fun NodeView(nodeState: NodeState) {
        ArrayNode(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            selected = selected,
            onTap = { onSelect(nodeState) },
            onPinAdd = { nodeState.inputPins.add(PinRowState(arrayElementPinFactory.createPinState())) }
        )
    }

    @Composable
    private fun Details() {
        actionDetails?.let {
            DetailsPanel(it)
        }
    }

    private fun onSelect(nodeState: NodeState) {
        if (selected) {
            selectionHandler.resetSelection()
            return
        }
        selectionHandler.onSelect(
            NodeStateSelectableWrapper(selectGetter = { selected },
                selectSetter = { selected = it },
                nodeState = nodeState,
                detailsComposable = { Details() })
        )
    }

    override fun toNode(nodeState: NodeState): Node {
        return Node(x = nodeState.x,
            y = nodeState.y,
            uniqueId = nodeState.id,
            inputPins = nodeState.inputPins.map { it.pinState }.map { it.pinDisplay.toPin(it) },
            outputPins = nodeState.outputPins.map { it.pinState }.map { it.pinDisplay.toPin(it) },
            tag = createNodeTypeTag(typeId = nodeEntity.id)
        )
    }
}