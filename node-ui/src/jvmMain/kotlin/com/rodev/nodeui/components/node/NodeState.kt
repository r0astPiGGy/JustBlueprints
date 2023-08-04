package com.rodev.nodeui.components.node

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowSnapshot
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import java.util.*

class NodeState(
    val id: String,
    val nodeDisplay: NodeDisplay,
    initialX: Float = 0f,
    initialY: Float = 0f,
) {
    private var pinDragListener: PinDragListener? = null
    var snapshotRequester by mutableStateOf<SnapshotRequester?>(null)

    val snapshotRequested by derivedStateOf {
        snapshotRequester?.snapshotRequested ?: false
    }

    val runtimeUUID = UUID.randomUUID().toString()

    val inputPins = mutableStateListOf<PinRowState>()
    val outputPins = mutableStateListOf<PinRowState>()

    var inputPinContainerPosition by mutableStateOf(Offset.Zero)
    var outputPinContainerPosition by mutableStateOf(Offset.Zero)

    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)

    fun onPinDragStart(pinState: PinState) {
        pinDragListener?.onPinDragStart(this, pinState)
    }

    fun onPinDrag(pinState: PinState, offset: Offset, change: PointerInputChange) {
        pinDragListener?.onPinDrag(pinState, offset, change)
    }

    fun onPinDragEnd(pinState: PinState) {
        pinDragListener?.onPinDragEnd()
    }

    fun setPinDragListener(pinDragListener: PinDragListener) {
        this.pinDragListener = pinDragListener
    }

    fun addSnapshot(pinRowSnapshot: PinRowSnapshot) {
        this.snapshotRequester?.addSnapshot(pinRowSnapshot)
    }

    @Composable
    fun NodeView() {
        nodeDisplay.NodeView(this)
    }
}