package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Pin
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.PinRepresentation
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.ConnectionType

class DefaultPinRepresentation(
    private val pinEntity: PinEntity,
    override val connectionType: ConnectionType,
) : PinRepresentation {
    override val supportsMultipleConnection: Boolean
        get() = pinEntity.supportsMultipleConnection

    override val name: String
        get() = pinEntity.name

    override val color: Int
        get() = pinEntity.color

    @Composable
    override fun onDraw(nodeState: NodeState, pinState: PinState, pinDragListener: PinDragListener) {
        Pin(
            nodeState = nodeState,
            pinState = pinState,
            pinDragListener = pinDragListener,
        ) {
            drawCircle(
                color = Color(pinEntity.color),
                style = if (pinState.connected) Fill else Stroke(width = 2f)
            )
        }
    }

}