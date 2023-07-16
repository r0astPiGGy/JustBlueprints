package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.presentation.components.pin.PinState
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowSnapshot
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowState
import com.rodev.jbpkmp.presentation.components.pin.row.SnapshotRequester
import com.rodev.jbpkmp.presentation.components.wire.TemporaryWire
import com.rodev.jbpkmp.presentation.components.wire.Wire
import com.rodev.jbpkmp.presentation.components.wire.WirePreview

class GraphViewModel(
    pinTypeComparator: PinTypeComparator = PinTypeComparator.Default
) : PinDragListener, SnapshotRequester {

    private val pinConnectionHandler = PinConnectionHandler(pinTypeComparator)
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
    private var currentHoveringRow: PinRowState? = null

    private val pinSnapshots = mutableSetOf<PinRowSnapshot>()

    fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                _nodeStates.add(NodeState(event.nodeEntity))
            }
            NodeClearEvent -> {
                _nodeStates.forEach(pinConnectionHandler::disconnectAll)
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

        val pinRowSnapshot = hitTest(endX, endY)
        val hoveredPin = pinRowSnapshot?.pinState
        val hoveredRow = pinRowSnapshot?.pinRowState

        if (hoveredPin != null && hoveredRow != null) {
            if (hoveredPin == currentDraggingPin) return

            clearCurrentHoveringRow()

            _temporaryLine.value = WirePreview(
                pinState.entity.color,
                hoveredPin.entity.color,
                start.x,
                start.y,
                hoveredPin.center.x,
                hoveredPin.center.y
            )

            currentHoveringRow = hoveredRow
            currentHoveringPin = hoveredPin
            hoveredRow.hovered = true
        } else {
            clearCurrentHoveringRow()
            currentHoveringPin = null
            _temporaryLine.value = TemporaryWire(
                pinState.entity.color,
                start.x,
                start.y,
                endX,
                endY
            )
        }
    }

    private fun clearCurrentHoveringRow() {
        val lastHoveredRow = currentHoveringRow
        if (lastHoveredRow != null) {
            lastHoveredRow.hovered = false
            currentHoveringRow = null
        }
    }

    private fun hitTest(x: Float, y: Float): PinRowSnapshot? {
        val lastHitTest = cachedHitTest
        if (lastHitTest != null && lastHitTest.isInBounds(x, y)) {
            return lastHitTest
        } else {
            cachedHitTest = null
        }

        for (pinSnapshot in pinSnapshots) {
            if (pinSnapshot.isInBounds(x, y)) {
                cachedHitTest = pinSnapshot
                return pinSnapshot
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
        currentHoveringPin = null
        cachedHitTest = null
        clearCurrentHoveringRow()
        _temporaryLine.value = null

        pinSnapshots.clear()
    }


}