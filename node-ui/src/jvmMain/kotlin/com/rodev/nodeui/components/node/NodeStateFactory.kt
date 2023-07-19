package com.rodev.nodeui.components.node

import com.rodev.nodeui.components.pin.row.PinRowStateFactory
import com.rodev.nodeui.model.Node

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