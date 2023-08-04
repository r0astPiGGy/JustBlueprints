package com.rodev.nodeui.components.graph

import androidx.compose.runtime.mutableStateListOf
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.*
import com.rodev.nodeui.components.pin.row.PinRowSnapshot
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.components.wire.PinWire
import com.rodev.nodeui.components.wire.Wire
import com.rodev.nodeui.components.wire.WireFactory
import com.rodev.nodeui.components.wire.getOpposite

class PinConnectionHandler(
    private val pinTypeComparator: PinTypeComparator,
    private val wireFactory: WireFactory
) {

    internal val mutableWires = mutableStateListOf<PinWire>()
    val wires: List<Wire>
        get() = mutableWires

    fun shouldAddSnapshot(
        snapshot: PinRowSnapshot,
        currentDraggingPin: PinState?,
        currentDraggingPinOwner: NodeState?
    ): Boolean {
        require(currentDraggingPin != null && currentDraggingPinOwner != null) {
            "Snapshot is available only when pin is being dragged"
        }

        val pinState = snapshot.pinRowState.pinState

        // Pre-filter
        if (pinState == currentDraggingPin) return false

        // Shouldn't connect pins with same node
        if (snapshot.nodeState == currentDraggingPinOwner) return false

        // Shouldn't connect pins with same connection type
        if (pinState.connectionTypeEquals(currentDraggingPin)) return false

        if (!pinTypeComparator.connectable(currentDraggingPin, pinState)) return false

        return !pinState.connectedTo(currentDraggingPin)
    }

    fun onConnection(initiator: PinState, connection: PinState?): Boolean {
        if (connection == null) return false

        val inputPin = if (initiator.isInput()) initiator else connection
        val outputPin = if (initiator.isOutput()) initiator else connection

        return handleConnection(initiator, inputPin, outputPin)
    }

    fun onDragStart(draggingPin: PinState) {
        if (!draggingPin.supportsMultipleConnection && draggingPin.isConnected()) {
            disconnectAll(draggingPin)
        }
    }

    private fun handleConnection(initiator: PinState, inputPin: PinState, outputPin: PinState): Boolean {
        require(inputPin != outputPin)
        require(inputPin.connectionTypeNotEquals(outputPin))

        // check types
        if (!pinTypeComparator.connectable(inputPin, outputPin)) return false

        val opposite = initiator.getOpposite(inputPin, outputPin)

        if (!opposite.supportsMultipleConnection && opposite.isConnected()) {
            // it can be connected, so it needs disconnection

            disconnectAll(opposite)
        }

        forceConnect(inputPin, outputPin)

        return true
    }

    fun connect(inputPin: PinState, outputPin: PinState) {
        require(inputPin != outputPin)
        require(inputPin.connectionTypeNotEquals(outputPin))
        require(pinTypeComparator.connectable(inputPin, outputPin))

        forceConnect(inputPin, outputPin)
    }

    private fun PinState.getOpposite(first: PinState, second: PinState): PinState {
        return if (first == this) second else first
    }

    private fun forceConnect(inputPin: PinState, outputPin: PinState) {
        val wire = wireFactory.createPinWire(inputPin, outputPin)

        inputPin.addWire(wire)
        outputPin.addWire(wire)

        mutableWires.add(wire)
    }

    private fun PinState.addWire(wire: PinWire) {
        val wasEmpty = connections.isEmpty()

        connections.add(wire)

        if (wasEmpty) {
            connected = true
        }
    }

    private fun PinState.removeWire(wire: PinWire) {
        connections.remove(wire)

        if (connections.isEmpty()) {
            connected = false
        }
    }

    private fun disconnectAll(pinState: PinState) {
        if (!pinState.isConnected()) return

        mutableWires.removeAll(pinState.connections)
        pinState.connections.forEach {
            val opposite = it.getOpposite(pinState)
            require(opposite != pinState)

            opposite.removeWire(it)
        }
        pinState.connections.clear()
        pinState.connected = false
    }

    fun disconnectAll(nodeState: NodeState) {
        disconnectAll(nodeState.inputPins)
        disconnectAll(nodeState.outputPins)
    }

    private fun disconnectAll(pins: List<PinRowState>) {
        pins.forEach { disconnectAll(it.pinState) }
    }

    private fun PinState.isConnected(): Boolean {
        return connected && connections.isNotEmpty()
    }

    private fun PinState.connectedTo(pinState: PinState): Boolean {
        for (wire in connections) {
            if (wire.inputPin == pinState || wire.outputPin == pinState)
                return true
        }

        return false
    }

}