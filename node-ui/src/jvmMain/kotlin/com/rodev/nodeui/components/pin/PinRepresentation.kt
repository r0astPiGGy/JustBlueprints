package com.rodev.nodeui.components.pin

import androidx.compose.runtime.Composable
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

interface PinRepresentation {

    val connectionType: ConnectionType

    val supportsMultipleConnection: Boolean

    val name: String

    val color: Int

    val type: Any?

    @Composable
    fun onDraw(nodeState: NodeState, pinState: PinState, pinDragListener: PinDragListener)

    fun toPin(pinState: PinState): Pin

}