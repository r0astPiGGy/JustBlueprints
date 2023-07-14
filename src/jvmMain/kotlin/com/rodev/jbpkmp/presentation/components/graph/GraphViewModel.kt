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

    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    private val _temporaryLine = mutableStateOf<Wire?>(null)
    val temporaryLine: State<Wire?>
        get() = _temporaryLine

    private var _snapshotRequested by mutableStateOf(false)

    override val snapshotRequested: Boolean
        get() = _snapshotRequested

    private var currentDraggingPin: PinState? = null
    private var currentHoveringPin: PinState? = null

    private val pinSnapshots = mutableSetOf<PinRowSnapshot>()

    fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                _nodeStates.add(NodeState(event.nodeEntity))
            }
        }
    }

    override fun onPinDragStart(pinState: PinState) {
        currentDraggingPin = pinState
        _snapshotRequested = true
    }

    override fun addSnapshot(snapshot: PinRowSnapshot) {
        val currentDraggingPin = currentDraggingPin

        require(currentDraggingPin != null) { "Snapshot is available only when pin is being dragged" }

        val pinState = snapshot.pinState

        // Pre-filter
        if (pinState == currentDraggingPin) return

        if (pinState.parent == currentDraggingPin.parent) return

        pinSnapshots.add(snapshot)
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
        for (pinSnapshot in pinSnapshots) {
            val top = pinSnapshot.topBound
            val bottom = pinSnapshot.bottomBound

            if (x in top.x..bottom.x && y in top.y..bottom.y) {
                return pinSnapshot.pinState
            }
        }
        return null
    }

    override fun onPinDragEnd() {
        _snapshotRequested = false
        currentDraggingPin = null
        clearCurrentHoveringPin()
        _temporaryLine.value = null

        pinSnapshots.clear()
    }


}