package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.row

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.PinRow
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.row.PinRowDisplay
import com.rodev.nodeui.components.pin.row.PinRowState

object DefaultInputPinRowDisplay : PinRowDisplay {

    @Composable
    override fun PinRowView(
        nodeState: NodeState,
        pinRowState: PinRowState
    ) {
        PinRow(
            nodeState = nodeState,
            pinRowState = pinRowState,
            containerPosition = nodeState.inputPinContainerPosition
        )
    }

}

object DefaultOutputPinRowDisplay : PinRowDisplay {

    @Composable
    override fun PinRowView(
        nodeState: NodeState,
        pinRowState: PinRowState
    ) {
        PinRow(
            nodeState = nodeState,
            pinRowState = pinRowState,
            containerPosition = nodeState.outputPinContainerPosition
        )
    }

}