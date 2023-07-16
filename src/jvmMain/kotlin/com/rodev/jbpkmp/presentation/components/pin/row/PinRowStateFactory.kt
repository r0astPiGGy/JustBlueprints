package com.rodev.jbpkmp.presentation.components.pin.row

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.Pin
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.presentation.components.pin.PinRow
import com.rodev.jbpkmp.presentation.components.pin.PinState
import com.rodev.jbpkmp.presentation.components.pin.PinStateFactory
import com.rodev.jbpkmp.util.MutableCoordinate

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

class DefaultPinRowStateFactory(pinStateFactory: PinStateFactory) : PinRowStateFactory(pinStateFactory) {

    override fun createRowRepresentation(pin: Pin, connectionType: ConnectionType): PinRowRepresentation {
        return DefaultPinRowRepresentation
    }

}

object DefaultPinRowRepresentation : PinRowRepresentation {

    @Composable
    override fun onDraw(
        nodeState: NodeState,
        pinRowState: PinRowState,
        pinDragListener: PinDragListener,
        snapshotRequester: SnapshotRequester,
        parentCoordinate: MutableCoordinate
    ) {
        PinRow(
            nodeState = nodeState,
            pinRowState = pinRowState,
            pinDragListener = pinDragListener,
            containerPosition = parentCoordinate,
            snapshotRequester = snapshotRequester
        )
    }

    override fun onHoverStart() {
    }

    override fun onHoverEnd() {
    }

}