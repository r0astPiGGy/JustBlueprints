package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.presentation.components.pin.*
import com.rodev.jbpkmp.presentation.components.wire.PinWire
import com.rodev.jbpkmp.presentation.components.wire.Wire

class PinConnectionHandler {

    private val _wires = mutableStateListOf<PinWire>()
    val wires: List<Wire>
        get() = _wires

    fun shouldAddSnapshot(snapshot: PinRowSnapshot, currentDraggingPin: PinState?): Boolean {
        require(currentDraggingPin != null) { "Snapshot is available only when pin is being dragged" }

        val pinState = snapshot.pinState

        // Pre-filter
        if (pinState == currentDraggingPin) return false

        // Shouldn't connect pins with same node
        if (pinState.parent == currentDraggingPin.parent) return false

        // Shouldn't connect pins with same connection type
        if (pinState.connectionTypeEquals(currentDraggingPin)) return false

        // todo Shouldn't add connected INPUT pins
        if (pinState.connectedTo(currentDraggingPin)) return false

        return true
    }

    fun onConnection(initiator: PinState, connection: PinState?) {
        if (connection == null) return

        val inputPin = if (initiator.isInput()) initiator else connection
        val outputPin = if (initiator.isOutput()) initiator else connection

        handleConnection(initiator, inputPin, outputPin)
    }

    fun onDragStart(draggingPin: PinState) {
        if (!draggingPin.supportsMultipleConnection() && draggingPin.isConnected()) {
            disconnectAll(draggingPin)
        }
    }

    // TODO add check for multiple connection
    private fun handleConnection(initiator: PinState, inputPin: PinState, outputPin: PinState) {
        require(inputPin != outputPin)
        require(inputPin.connectionTypeNotEquals(outputPin))

        // check types
        if (!PinTypeComparator.connectable(inputPin, outputPin)) return

        if (initiator.isOutput() && inputPin.isConnected()) {
            // inputPin can be connected, so it needs disconnection

            disconnectAll(inputPin)
        }

        connect(inputPin, outputPin)
    }

    private fun connect(inputPin: PinState, outputPin: PinState) {
        val wire = PinWire(inputPin, outputPin)

        inputPin.addWire(wire)
        outputPin.addWire(wire)

        _wires.add(wire)
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
        _wires.removeAll(pinState.connections)
        pinState.connections.forEach {
            val other = if (it.inputPin == pinState) it.outputPin else it.inputPin
            require(other != pinState)

            other.removeWire(it)
        }
        pinState.connections.clear()
        pinState.connected = false
    }

    private fun PinState.isConnected(): Boolean {
        return connected && connections.isNotEmpty()
    }

    private fun PinState.supportsMultipleConnection(): Boolean {
        return entity.supportsMultipleConnection
    }

    private fun PinState.connectedTo(pinState: PinState): Boolean {
        for (wire in connections) {
            if (wire.inputPin == pinState || wire.outputPin == pinState)
                return true
        }

        return false
    }

}