package com.rodev.nodeui.components.pin

import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

abstract class PinStateFactory {
    open fun createInputPinState(pin: Pin): PinState {
        return createPinState(pin, ConnectionType.INPUT)
    }

    open fun createOutputPinState(pin: Pin): PinState {
        return createPinState(pin, ConnectionType.OUTPUT)
    }

    open fun createPinState(pin: Pin, connectionType: ConnectionType): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(pin, connectionType)
        ).apply {
            defaultValueComposable.setValue(pin.value)
        }
    }

    protected abstract fun createPinRepresentation(pin: Pin, connectionType: ConnectionType): PinRepresentation

}