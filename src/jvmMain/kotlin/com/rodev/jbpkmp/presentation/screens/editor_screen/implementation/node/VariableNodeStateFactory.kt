package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import com.rodev.generator.action.entity.PinType
import com.rodev.jbpkmp.domain.repository.PinTypeDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Pin as PinComposable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.row.DefaultOutputPinRowDisplay
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Node
import com.rodev.nodeui.model.Pin

class VariableNodeStateFactory(
    private val selectionHandler: SelectionHandler,
    private val variableStateProvider: VariableStateProvider,
    private val pinTypeDataSource: PinTypeDataSource
) : NodeStateFactory {
    override fun createNodeState(node: Node): NodeState {
        val outputPin = node.outputPins[0]
        val variableId = node.tag.getString(VARIABLE_ID_TAG)
        val pinType = pinTypeDataSource.getPinTypeById(VARIABLE_TYPE_TAG)

        require(variableId != null) { "Invalid node provided: No $variableId passed" }
        require(pinType != null) { "PinType by id $VARIABLE_TYPE_TAG not found" }

        return NodeState(
            id = node.uniqueId,
            initialX = node.x,
            initialY = node.y,
            nodeDisplay = VariableNodeDisplay(
                selectionHandler = selectionHandler,
                variableState = variableStateProvider.getVariableStateById(variableId)!!,
                variableId = variableId
            )
        ).apply {
            outputPins.add(
                PinRowState(
                    pinRowDisplay = DefaultOutputPinRowDisplay,
                    pinState = PinState(
                        id = outputPin.uniqueId,
                        connectionType = ConnectionType.OUTPUT,
                        supportsMultipleConnection = true,
                        pinDisplay = VariablePinDisplay(
                            uniqueId = outputPin.uniqueId,
                            type = pinType
                        )
                    )
                )
            )
        }
    }
}

private class VariablePinDisplay(
    private val uniqueId: String,
    override val type: PinType
) : PinDisplay {
    override val name: String
        get() = ""
    override val color: Int
        get() = type.color

    @Composable
    override fun PinView(nodeState: NodeState, pinState: PinState) {
        PinComposable(
            nodeState = nodeState,
            pinState = pinState
        ) {
            DefaultDrawFunction.drawPin(this, pinState)
        }
    }

    override fun toPin(pinState: PinState): Pin {
        return createPin(
            id = uniqueId,
            typeId = VARIABLE_TYPE_TAG,
            value = pinState.defaultValueComposable.getValue()
        )
    }
}
