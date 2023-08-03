package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.repository.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableStateProvider
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.row.PinRowStateFactory
import com.rodev.nodeui.model.Node

class DefaultNodeStateFactory(
    private val pinRowStateFactory: PinRowStateFactory,
    private val nodeDataSource: NodeDataSource,
    private val actionDataSource: ActionDataSource,
    private val nodeTypeDataSource: NodeTypeDataSource,
    private val selectionHandler: SelectionHandler,
    private val variableStateProvider: VariableStateProvider
) : NodeStateFactory(pinRowStateFactory) {

    override fun createNodeState(node: Node): NodeState {
        if (node.typeId == variableTypeId) {
            return createVariableNodeState(node)
        }

        return super.createNodeState(node)
    }

    private fun createVariableNodeState(node: Node): NodeState {
        val outputPin = node.outputPins[0]
        val variableId = outputPin.typeId

        return NodeState(
            id = node.uniqueId,
            initialX = node.x,
            initialY = node.y,
            nodeRepresentation = VariableNodeRepresentation(
                selectionHandler,
                variableState = variableStateProvider.getVariableStateById(variableId)!!,
                variableId = variableId
            )
        ).apply {
            outputPins.add(
                pinRowStateFactory.createOutputPinRowState(node, outputPin)
            )
        }
    }

    override fun getNodeRepresentation(typeId: String): NodeRepresentation {
        val node = nodeDataSource.getNodeModelById(typeId)
        val nodeType = nodeTypeDataSource[node.type]!!
        val action = actionDataSource.getActionById(typeId)

        return DefaultNodeRepresentation(
            NodeEntity(
                id = typeId,
                header = action.name,
                headerColor = nodeType.color,
                iconPath = action.iconPath
            ),
            selectionHandler
        )
    }

}

