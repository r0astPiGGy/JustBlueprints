package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.PinRow
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.PinRowRepresentation
import com.rodev.nodeui.components.pin.row.PinRowState
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.util.MutableCoordinate

object DefaultPinRowRepresentation : PinRowRepresentation {

    @Composable
    override fun onDraw(
        nodeState: NodeState,
        pinRowState: PinRowState,
        pinDragListener: PinDragListener,
        snapshotRequester: SnapshotRequester,
        parentCoordinate: MutableCoordinate
    ) {
        PinRow(
            nodeState = nodeState,
            pinRowState = pinRowState,
            pinDragListener = pinDragListener,
            containerPosition = parentCoordinate,
            snapshotRequester = snapshotRequester
        )
    }

    override fun onHoverStart() {
    }

    override fun onHoverEnd() {
    }

}