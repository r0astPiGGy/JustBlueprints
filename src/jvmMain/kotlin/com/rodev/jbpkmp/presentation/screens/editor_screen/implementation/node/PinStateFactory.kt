package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.extra_data.EnumExtraData
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.domain.repository.DefaultValueComposableRegistry
import com.rodev.jbpkmp.domain.repository.PinTypeDataSource
import com.rodev.jbpkmp.domain.repository.get
import com.rodev.jbpkmp.presentation.screens.editor_screen.getId
import com.rodev.jbpkmp.presentation.screens.editor_screen.getValue
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.BooleanInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.EnumInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.StringInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinDisplay
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinDrawFunction
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.visibleIfNotConnected
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

class PinStateFactory(
    private val pinTypeDataSource: PinTypeDataSource
) {

    private val defaultValueComposableRegistry = createDefaultValueComposableRegistry()

    private fun createPinDisplay(pinModel: PinModel): PinDisplay {
        val pinType = pinTypeDataSource[pinModel.type]!!

        val execPin = pinType.id == "exec"

        val pinEntity = PinEntity(
            id = pinModel.id,
            color = pinType.color,
            name = pinModel.label,
            type = pinType
        )

        val drawFunction: PinDrawFunction = if (execPin) {
            ExecDrawFunction
        } else {
            DefaultDrawFunction
        }

        return DefaultPinDisplay(pinEntity, drawFunction)
    }

    fun createInputPinState(nodeModel: NodeModel, pin: Pin): PinState {
        val pinValue = pin.getValue()
        val pinTypeId = pin.getId()
        val pinModel = nodeModel.findPinModelById(pinTypeId, ConnectionType.INPUT)

        return PinState(
            id = pin.uniqueId,
            connectionType = ConnectionType.INPUT,
            supportsMultipleConnection = false,
            pinDisplay = createPinDisplay(pinModel),
            defaultValueComposable = defaultValueComposableRegistry.create(pinModel).visibleIfNotConnected()
        ).apply {
            defaultValueComposable.setValue(pinValue)
        }
    }

    fun createOutputPinState(nodeModel: NodeModel, pin: Pin): PinState {
        val pinValue = pin.getValue()
        val pinTypeId = pin.getId()
        val pinModel = nodeModel.findPinModelById(pinTypeId, ConnectionType.OUTPUT)
        val isExec = pinModel.type == "exec"

        return PinState(
            id = pin.uniqueId,
            connectionType = ConnectionType.OUTPUT,
            supportsMultipleConnection = !isExec,
            pinDisplay = createPinDisplay(pinModel),
            defaultValueComposable = defaultValueComposableRegistry.create(pinModel)
        ).apply {
            defaultValueComposable.setValue(pinValue)
        }
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

private fun createDefaultValueComposableRegistry() = DefaultValueComposableRegistry.create {
    register("text") {
        StringInputComposable()
    }
    register("enum") {
        val extra = it.extra as EnumExtraData

        EnumInputComposable(extra.values)
    }
    register("boolean") {
        BooleanInputComposable()
    }
}