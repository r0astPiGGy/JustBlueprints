package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.*
import com.rodev.jbpkmp.presentation.components.wire.PinWire
import com.rodev.jbpkmp.presentation.components.wire.Wire
import com.rodev.jbpkmp.presentation.components.wire.getOpposite
import com.rodev.jbpkmp.presentation.components.wire.getPin

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
        if (pinState.getNode() == currentDraggingPin.getNode()) return false

        // Shouldn't connect pins with same connection type
        if (pinState.connectionTypeEquals(currentDraggingPin)) return false

        return !pinState.connectedTo(currentDraggingPin)
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

    private fun handleConnection(initiator: PinState, inputPin: PinState, outputPin: PinState) {
        require(inputPin != outputPin)
        require(inputPin.connectionTypeNotEquals(outputPin))

        // check types
        if (!PinTypeComparator.connectable(inputPin, outputPin)) return

        val opposite = initiator.getOpposite(inputPin, outputPin)

        if (!opposite.supportsMultipleConnection() && opposite.isConnected()) {
            // it can be connected, so it needs disconnection

            disconnectAll(opposite)
        }

        connect(inputPin, outputPin)
    }

    private fun PinState.getOpposite(first: PinState, second: PinState): PinState {
        return if (first == this) second else first
    }

    private fun connect(inputPin: PinState, outputPin: PinState) {
        val wire = PinWire(
            inputPin = inputPin,
            outputPin = outputPin
        )

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
            val opposite = it.getOpposite(pinState)
            require(opposite != pinState)

            opposite.removeWire(it)
        }
        pinState.connections.clear()
        pinState.connected = false
    }

    fun disconnectAll(nodeState: NodeState) {
        getConnectedPins(nodeState).forEach(::disconnectAll)
    }

    fun getConnectedPins(nodeState: NodeState): Collection<PinState> {
        return _wires.mapNotNull { it.getPin(owner = nodeState) }
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