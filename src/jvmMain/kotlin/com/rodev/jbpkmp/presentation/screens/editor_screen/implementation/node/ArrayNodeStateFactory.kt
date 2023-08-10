package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.extra_data.EventExtraData
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.repository.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.createPin
import com.rodev.jbpkmp.presentation.screens.editor_screen.getType
import com.rodev.jbpkmp.util.castTo
import com.rodev.jbpkmp.util.contains
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin
import java.util.UUID

class ArrayNodeStateFactory(
    private val nodeDataSource: NodeDataSource,
    private val nodeTypeDataSource: NodeTypeDataSource,
    private val actionDataSource: ActionDataSource,
    private val selectionHandler: SelectionHandler,
    private val actionDetailsDataSource: ActionDetailsDataSource,
    selectorDataSource: SelectorDataSource,
    pinTypeDataSource: PinTypeDataSource
) : NodeStateFactory, ArrayElementPinFactory {

    private val pinStateFactory = PinStateFactory(pinTypeDataSource, selectorDataSource)

    override fun createElement(): PinModel {
        return PinModel(
            id = "",
            type = "any",
        )
    }

    override fun createPinState(id: String): PinState {
        return pinStateFactory.createInputPinState(
            createPin(id = id, typeId = ""),
            createElement()
        )
    }

    override fun createNodeState(node: Node): NodeState {
        val typeId = node.getType()
        val nodeModel = nodeDataSource.getNodeModelById(typeId)!!

        val nodeState = NodeState(
            id = node.uniqueId,
            initialX = node.x,
            initialY = node.y,
            nodeDisplay = getNodeRepresentation(typeId)
        )

        node.inputPins.map {
            createPinRowState(
                pinState = createPinState(id = it.uniqueId)
            )
        }.let {
            nodeState.inputPins.addAll(it)
        }

        node.outputPins.map {
            createPinRowState(
                pinState = pinStateFactory.createOutputPinState(nodeModel, it)
            )
        }.let {
            nodeState.outputPins.addAll(it)
        }

        return nodeState
    }

    private fun createPinRowState(pinState: PinState): PinRowState {
        return PinRowState(
            pinState = pinState
        )
    }

    private fun getNodeRepresentation(typeId: String): NodeDisplay {
        val node = nodeDataSource.getNodeModelById(typeId)!!
        val nodeType = nodeTypeDataSource[node.type]!!
        val action = actionDataSource.getActionById(typeId)

        return ArrayNodeDisplay(
            NodeEntity(
                id = typeId,
                header = action.name,
                subHeader = null,
                headerColor = nodeType.color,
                iconPath = action.iconPath
            ),
            selectionHandler,
            actionDetailsDataSource[node.id],
            this
        )
    }
}

interface ArrayElementPinFactory {

    fun createElement(): PinModel

    fun createPinState(id: String = UUID.randomUUID().toString()): PinState

}