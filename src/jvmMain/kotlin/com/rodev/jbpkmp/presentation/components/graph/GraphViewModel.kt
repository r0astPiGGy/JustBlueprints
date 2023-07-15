package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.presentation.components.pin.PinRowSnapshot
import com.rodev.jbpkmp.presentation.components.pin.PinState
import com.rodev.jbpkmp.presentation.components.pin.SnapshotRequester
import com.rodev.jbpkmp.presentation.components.wire.TemporaryWire
import com.rodev.jbpkmp.presentation.components.wire.Wire
import com.rodev.jbpkmp.presentation.components.wire.WirePreview

class GraphViewModel : PinDragListener, SnapshotRequester {

    private val pinConnectionHandler = PinConnectionHandler()
    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    private val _temporaryLine = mutableStateOf<Wire?>(null)
    val temporaryLine: State<Wire?>
        get() = _temporaryLine

    val lines: List<Wire>
        get() = pinConnectionHandler.wires

    private var _snapshotRequested by mutableStateOf(false)

    override val snapshotRequested: Boolean
        get() = _snapshotRequested

    private var cachedHitTest: PinRowSnapshot? = null

    private var currentDraggingPin: PinState? = null
    private var currentHoveringPin: PinState? = null

    private val pinSnapshots = mutableSetOf<PinRowSnapshot>()

    fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                _nodeStates.add(NodeState(event.nodeEntity))
            }
            NodeClearEvent -> {
                _nodeStates.clear()
            }
        }
    }

    override fun onPinDragStart(pinState: PinState) {
        pinConnectionHandler.onDragStart(pinState)

        currentDraggingPin = pinState
        _snapshotRequested = true
    }

    override fun addSnapshot(snapshot: PinRowSnapshot) {
        if (pinConnectionHandler.shouldAddSnapshot(snapshot, currentDraggingPin)) {
            pinSnapshots.add(snapshot)
        }
    }

    override fun onPinDrag(pinState: PinState, offset: Offset, change: PointerInputChange) {
        require(pinState == currentDraggingPin)

        val pos = pinState.position
        val start = pinState.center

        val endX = pos.x + change.position.x
        val endY = pos.y + change.position.y

        val hoveredPin = hitTest(endX, endY)

        if (hoveredPin != null) {
            if (hoveredPin == currentDraggingPin) return

            clearCurrentHoveringPin()

            _temporaryLine.value = WirePreview(
                pinState.entity.color,
                hoveredPin.entity.color,
                start.x,
                start.y,
                hoveredPin.center.x,
                hoveredPin.center.y
            )

            currentHoveringPin = hoveredPin
            hoveredPin.rowHovered = true
        } else {
            clearCurrentHoveringPin()
            _temporaryLine.value = TemporaryWire(
                pinState.entity.color,
                start.x,
                start.y,
                endX,
                endY
            )
        }
    }

    private fun clearCurrentHoveringPin() {
        val lastHoveredPin = currentHoveringPin
        if (lastHoveredPin != null) {
            lastHoveredPin.rowHovered = false
            currentHoveringPin = null
        }
    }

    private fun hitTest(x: Float, y: Float): PinState? {
        val lastHitTest = cachedHitTest
        if (lastHitTest != null && lastHitTest.isInBounds(x, y)) {
            return lastHitTest.pinState
        } else {
            cachedHitTest = null
        }

        for (pinSnapshot in pinSnapshots) {
            if (pinSnapshot.isInBounds(x, y)) {
                cachedHitTest = pinSnapshot
                return pinSnapshot.pinState
            }
        }
        return null
    }

    private fun PinRowSnapshot.isInBounds(x: Float, y: Float): Boolean {
        return x in topBound.x..bottomBound.x && y in topBound.y..bottomBound.y
    }

    override fun onPinDragEnd() {
        pinConnectionHandler.onConnection(currentDraggingPin!!, currentHoveringPin)

        _snapshotRequested = false
        currentDraggingPin = null
        cachedHitTest = null
        clearCurrentHoveringPin()
        _temporaryLine.value = null

        pinSnapshots.clear()
    }


}