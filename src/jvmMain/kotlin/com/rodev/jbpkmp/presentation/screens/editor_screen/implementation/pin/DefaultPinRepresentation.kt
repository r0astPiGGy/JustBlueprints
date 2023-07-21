package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.PinRepresentation
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Pin as PinComposable

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

    override val type: Any
        get() = pinEntity.type

    @Composable
    override fun onDraw(nodeState: NodeState, pinState: PinState, pinDragListener: PinDragListener) {
        PinComposable(
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

    override fun toPin(pinState: PinState): Pin {
        require(pinState.pinRepresentation == this)

        return Pin(
            uniqueId = pinState.id,
            typeId = pinEntity.id,
            value = pinState.defaultValueComposable.getValue()
        )
    }

}