package com.rodev.nodeui.components.pin.row

import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.PinStateFactory
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin

abstract class PinRowStateFactory(
    private val pinStateFactory: PinStateFactory
) {

    fun createInputPinRowState(node: Node, pin: Pin): PinRowState {
        return createPinRowState(node, pin, pinStateFactory.createInputPinState(node, pin), ConnectionType.INPUT)
    }

    fun createOutputPinRowState(node: Node, pin: Pin): PinRowState {
        return createPinRowState(node, pin, pinStateFactory.createOutputPinState(node, pin), ConnectionType.OUTPUT)
    }

    protected open fun createPinRowState(node: Node, pin: Pin, pinState: PinState, connectionType: ConnectionType): PinRowState {
        return PinRowState(
            pinRowRepresentation = createRowRepresentation(node, pin, connectionType),
            pinState = pinState
        )
    }

    protected abstract fun createRowRepresentation(node: Node, pin: Pin, connectionType: ConnectionType): PinRowRepresentation

}