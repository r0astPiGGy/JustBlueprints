package com.rodev.nodeui.components.pin

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.rodev.nodeui.components.node.NodeState

interface PinDragListener {

    fun onPinDragStart(pinOwner: NodeState, pinState: PinState)

    fun onPinDrag(pinState: PinState, offset: Offset, change: PointerInputChange)

    fun onPinDragEnd()

}

fun Modifier.pinDragModifier(nodeState: NodeState, pinState: PinState): Modifier {
    return this.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = {
                nodeState.onPinDragStart(pinState)
            },
            onDragEnd = {
                nodeState.onPinDragEnd(pinState)
            }
        ) { change: PointerInputChange, dragAmount: Offset ->
            nodeState.onPinDrag(pinState, dragAmount, change)
            change.consume()
        }
    }
}