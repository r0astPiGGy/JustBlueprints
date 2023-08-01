package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.PinDrawFunction
import com.rodev.nodeui.components.pin.PinRepresentation
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Pin as PinComposable

class DefaultPinRepresentation(
    private val pinEntity: PinEntity,
    override val connectionType: ConnectionType,
    private val pinDrawFunction: PinDrawFunction
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
            pinDrawFunction.drawPin(this, pinState)
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