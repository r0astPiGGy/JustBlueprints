package com.rodev.nodeui.components.pin.row

import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.PinStateFactory
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

abstract class PinRowStateFactory(
    private val pinStateFactory: PinStateFactory
) {

    fun createInputPinRowState(pin: Pin): PinRowState {
        return createPinRowState(pin, pinStateFactory.createInputPinState(pin), ConnectionType.INPUT)
    }

    fun createOutputPinRowState(pin: Pin): PinRowState {
        return createPinRowState(pin, pinStateFactory.createOutputPinState(pin), ConnectionType.OUTPUT)
    }

    protected open fun createPinRowState(pin: Pin, pinState: PinState, connectionType: ConnectionType): PinRowState {
        return PinRowState(
            pinRowRepresentation = createRowRepresentation(pin, connectionType),
            pinState = pinState
        )
    }

    protected abstract fun createRowRepresentation(pin: Pin, connectionType: ConnectionType): PinRowRepresentation

}