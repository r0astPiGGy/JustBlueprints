package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler.Default.onSelect
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ArrayNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.createNodeTypeTag
import com.rodev.jbpkmp.util.generateUniqueId
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.Node

class ArrayNodeDisplay(
    private val nodeEntity: NodeEntity,
    private val arrayElementPinFactory: ArrayElementPinFactory,
    selectionHandler: SelectionHandler,
    actionDetails: ActionDetails?
) : DefaultNodeDisplay(nodeEntity, selectionHandler, actionDetails) {

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
}