package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.Pin
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.util.randomPinEntity
import com.rodev.jbpkmp.presentation.components.pin.Pin as PinComposable

abstract class PinStateFactory {
    open fun createInputPinState(pin: Pin): PinState {
        return createPinState(pin, ConnectionType.INPUT)
    }

    open fun createOutputPinState(pin: Pin): PinState {
        return createPinState(pin, ConnectionType.OUTPUT)
    }

    protected open fun createPinState(pin: Pin, connectionType: ConnectionType): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(pin, connectionType)
        ).apply {
            defaultValueComposable.setValue(pin.value)
        }
    }

    protected abstract fun createPinRepresentation(pin: Pin, connectionType: ConnectionType): PinRepresentation

}

class DefaultPinStateFactory : PinStateFactory() {

    override fun createInputPinState(pin: Pin): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(pin, ConnectionType.INPUT),
            defaultValueComposable = StringInputComposable().visibleIfNotConnected()
        )
    }

    override fun createPinRepresentation(pin: Pin, connectionType: ConnectionType): PinRepresentation {
        return DefaultPinRepresentation(randomPinEntity(connectionType), connectionType)
    }

}

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

}