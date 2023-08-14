package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.repository.IconDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ArrayNode
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.row.PinRowState

class ArrayNodeDisplay(
    private val nodeEntity: NodeEntity,
    private val arrayElementPinFactory: ArrayElementPinFactory,
    private val iconDataSource: IconDataSource,
    selectionHandler: SelectionHandler,
    actionDetails: ActionDetails?
) : DefaultNodeDisplay(nodeEntity, selectionHandler, iconDataSource, actionDetails) {

    @Composable
    override fun NodeView(nodeState: NodeState) {
        ArrayNode(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            selected = selected,
            onTap = { onSelect(nodeState) },
            onPinAdd = { nodeState.inputPins.add(PinRowState(arrayElementPinFactory.createPinState())) },
            iconDataSource = iconDataSource
        )
    }
}