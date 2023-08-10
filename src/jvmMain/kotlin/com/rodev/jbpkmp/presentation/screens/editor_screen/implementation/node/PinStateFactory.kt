package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.NodeModel
import com.rodev.generator.action.entity.PinModel
import com.rodev.generator.action.entity.SelectorType
import com.rodev.generator.action.entity.extra_data.CompoundExtraData
import com.rodev.generator.action.entity.extra_data.EnumExtraData
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.domain.repository.DefaultValueComposableRegistry
import com.rodev.jbpkmp.domain.repository.PinTypeDataSource
import com.rodev.jbpkmp.domain.repository.SelectorDataSource
import com.rodev.jbpkmp.domain.repository.get
import com.rodev.jbpkmp.presentation.screens.editor_screen.getId
import com.rodev.jbpkmp.presentation.screens.editor_screen.getValue
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.BooleanInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.DecimalInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.EnumInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.SelectorInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.StringInputComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinDisplay
import com.rodev.jbpkmp.util.castTo
import com.rodev.nodeui.components.pin.EmptyDefaultValueComposable
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.visibleIfNotConnected
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

class PinStateFactory(
    private val pinTypeDataSource: PinTypeDataSource,
    selectorDataSource: SelectorDataSource
) {

    private val defaultValueComposableRegistry =
        createDefaultValueComposableRegistry(selectorDataSource)

    private fun createPinDisplay(pinModel: PinModel): PinDisplay {
        val pinType = pinTypeDataSource[pinModel.type]!!

        val pinEntity = PinEntity(
            id = pinModel.id,
            color = pinType.color,
            name = pinModel.label,
            type = pinType
        )

        return DefaultPinDisplay(pinEntity, pinModel.extra)
    }

    fun createInputPinState(nodeModel: NodeModel, pin: Pin): PinState {
        val pinTypeId = pin.getId()
        val pinModel = nodeModel.findPinModelById(pinTypeId, ConnectionType.INPUT)

        return createInputPinState(pin, pinModel)
    }

    fun createInputPinState(pin: Pin, pinModel: PinModel): PinState {
        val pinValue = pin.getValue()

        return PinState(
            id = pin.uniqueId,
            connectionType = ConnectionType.INPUT,
            supportsMultipleConnection = false,
            pinDisplay = createPinDisplay(pinModel),
            defaultValueComposable = defaultValueComposableRegistry.create(pinModel)
                .visibleIfNotConnected()
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
            defaultValueComposable = EmptyDefaultValueComposable
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

private fun createDefaultValueComposableRegistry(selectorDataSource: SelectorDataSource) =
    DefaultValueComposableRegistry.create {
        register("text") {
            StringInputComposable()
        }
        SelectorType.values().forEach { selectorType ->
            register(selectorType.id) {
                SelectorInputComposable(
                    selectorDataSource.getSelectorByType(selectorType).selectorList
                )
            }
        }
        register("number") {
            DecimalInputComposable()
        }
        register("enum") {
            val extra = it.extra.castTo<EnumExtraData>()

            EnumInputComposable(extra.values)
        }
        register("boolean") {
            BooleanInputComposable()
        }
    }
