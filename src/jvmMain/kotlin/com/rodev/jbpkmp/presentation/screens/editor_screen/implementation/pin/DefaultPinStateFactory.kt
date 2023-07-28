package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.domain.repository.NodeDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.StringInputComposable
import com.rodev.jbpkmp.util.randomColor
import com.rodev.nodeui.components.pin.PinRepresentation
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.PinStateFactory
import com.rodev.nodeui.components.pin.visibleIfNotConnected
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin

class DefaultPinStateFactory(
    private val nodeDataSource: NodeDataSource
) : PinStateFactory() {

    override fun createInputPinState(node: Node, pin: Pin): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(node, pin, ConnectionType.INPUT),
            defaultValueComposable = StringInputComposable().visibleIfNotConnected()
        ).apply {
            defaultValueComposable.setValue(pin.value)
        }
    }

    override fun createPinRepresentation(node: Node, pin: Pin, connectionType: ConnectionType): PinRepresentation {
        val nodeModel = nodeDataSource.getNodeModelById(node.typeId)
        val pinModel = nodeModel.findPinModelById(pin.typeId, connectionType)

        val pinEntity = PinEntity(
            id = pin.typeId,
            color = randomColor(),
            name = pinModel.label,
            connectionType = connectionType
        )

        return DefaultPinRepresentation(pinEntity, connectionType)
    }

    private fun NodeModel.findPinModelById(id: String, connectionType: ConnectionType): PinModel {
        return when (connectionType) {
            ConnectionType.INPUT -> findPinModelByIdIn(input, id)
            ConnectionType.OUTPUT -> findPinModelByIdIn(output, id)
        }
    }

    private fun findPinModelByIdIn(pins: List<PinModel>, id: String): PinModel {
        return pins.find { it.id == id }!!
    }
}