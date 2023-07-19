package com.rodev.nodeui.components.pin

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.rodev.nodeui.components.node.NodeState

interface PinDragListener {

    fun onPinDragStart(pinOwner: NodeState, pinState: PinState)

    fun onPinDrag(pinState: PinState, offset: Offset, change: PointerInputChange)

    fun onPinDragEnd()

}