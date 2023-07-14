package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.jbpkmp.presentation.components.Wire
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragHandler
import com.rodev.jbpkmp.presentation.components.pin.PinState

class GraphViewModel : PinDragHandler {

    private val _nodeStates = mutableStateListOf<NodeState>()
    val nodeStates: List<NodeState>
        get() = _nodeStates

    private val _temporaryLine = mutableStateOf<Wire?>(null)
    val temporaryLine: State<Wire?>
        get() = _temporaryLine

    private var currentDraggingPin: PinState? = null

    fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                _nodeStates.add(NodeState(event.nodeEntity))
            }
        }
    }

    override fun onDragStart(pinState: PinState) {
        currentDraggingPin = pinState
    }

    override fun onDrag(pinState: PinState, offset: Offset, change: PointerInputChange) {
        require(pinState == currentDraggingPin)

        val pos = pinState.position
        val start = pinState.center

        _temporaryLine.value = Wire(
            start.x,
            start.y,
            pos.x + change.position.x,
            pos.y + change.position.y
        )
    }

    override fun onEnd() {
        currentDraggingPin = null
        _temporaryLine.value = null
    }


}