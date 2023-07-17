package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.presentation.components.node.NodeState

interface PinRepresentation {

    val connectionType: ConnectionType

    val supportsMultipleConnection: Boolean

    val name: String

    val color: Int

    @Composable
    fun onDraw(nodeState: NodeState, pinState: PinState, pinDragListener: PinDragListener)

}