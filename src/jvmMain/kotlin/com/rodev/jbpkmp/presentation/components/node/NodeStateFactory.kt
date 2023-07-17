package com.rodev.jbpkmp.presentation.components.node

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.data.Node
import com.rodev.jbpkmp.data.NodeEntity
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowStateFactory
import com.rodev.jbpkmp.presentation.components.pin.row.SnapshotRequester
import com.rodev.jbpkmp.util.randomNodeEntity
import com.rodev.jbpkmp.presentation.components.node.SimpleNode as NodeComposable

abstract class NodeStateFactory(
    private val pinRowStateFactory: PinRowStateFactory
) {

    open fun createNodeState(node: Node): NodeState {
        val nodeState = NodeState(
            id = node.uniqueId,
            nodeRepresentation = getNodeRepresentation(node.typeId),
            initialX = node.x,
            initialY = node.y
        )

        node.inputPins
            .map(pinRowStateFactory::createInputPinRowState)
            .forEach(nodeState.inputPins::add)

        node.outputPins
            .map(pinRowStateFactory::createOutputPinRowState)
            .forEach(nodeState.outputPins::add)


        return nodeState
    }

    protected abstract fun getNodeRepresentation(typeId: String): NodeRepresentation

}

class DefaultNodeStateFactory(pinRowStateFactory: PinRowStateFactory) : NodeStateFactory(pinRowStateFactory) {
    override fun getNodeRepresentation(typeId: String): NodeRepresentation {
        return DefaultNodeRepresentation(randomNodeEntity())
    }
}

class DefaultNodeRepresentation(
    private val nodeEntity: NodeEntity
) : NodeRepresentation {

    @Composable
    override fun onDraw(nodeState: NodeState, pinDragListener: PinDragListener, snapshotRequester: SnapshotRequester) {
        NodeComposable(
            nodeState = nodeState,
            nodeEntity = nodeEntity,
            pinDragListener = pinDragListener,
            snapshotRequester = snapshotRequester
        )
    }

}