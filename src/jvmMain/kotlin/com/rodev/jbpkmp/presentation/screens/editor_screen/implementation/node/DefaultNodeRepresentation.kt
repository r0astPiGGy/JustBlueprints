package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.SimpleNode
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.model.Node

class DefaultNodeRepresentation(
    private val nodeEntity: NodeEntity
) : NodeRepresentation {

    @Composable
    override fun onDraw(nodeState: NodeState, pinDragListener: PinDragListener, snapshotRequester: SnapshotRequester) {
        SimpleNode(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            pinDragListener = pinDragListener,
            snapshotRequester = snapshotRequester
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