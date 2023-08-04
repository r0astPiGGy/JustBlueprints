package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.createPinTag
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDrawFunction
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.Pin
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Pin as PinComposable

class DefaultPinDisplay(
    private val pinEntity: PinEntity,
    private val pinDrawFunction: PinDrawFunction
) : PinDisplay {

    override val name: String
        get() = pinEntity.name

    override val color: Int
        get() = pinEntity.color

    override val type: Any
        get() = pinEntity.type

    @Composable
    override fun PinView(nodeState: NodeState, pinState: PinState) {
        PinComposable(
            nodeState = nodeState,
            pinState = pinState
        ) {
            pinDrawFunction.drawPin(this, pinState)
        }
    }

    override fun toPin(pinState: PinState): Pin {
        require(pinState.pinDisplay == this)

        return Pin(
            uniqueId = pinState.id,
            createPinTag(
                typeId = pinEntity.id,
                value = pinState.defaultValueComposable.getValue()
            )
        )
    }

}