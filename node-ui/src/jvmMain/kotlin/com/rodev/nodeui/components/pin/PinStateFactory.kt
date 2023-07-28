package com.rodev.nodeui.components.pin

import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin

abstract class PinStateFactory {
    open fun createInputPinState(node: Node, pin: Pin): PinState {
        return createPinState(node, pin, ConnectionType.INPUT)
    }

    open fun createOutputPinState(node: Node, pin: Pin): PinState {
        return createPinState(node, pin, ConnectionType.OUTPUT)
    }

    open fun createPinState(node: Node, pin: Pin, connectionType: ConnectionType): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(node, pin, connectionType)
        ).apply {
            defaultValueComposable.setValue(pin.value)
        }
    }

    protected abstract fun createPinRepresentation(node: Node, pin: Pin, connectionType: ConnectionType): PinRepresentation

}