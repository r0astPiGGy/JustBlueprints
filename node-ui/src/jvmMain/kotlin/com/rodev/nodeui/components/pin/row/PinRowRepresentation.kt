package com.rodev.nodeui.components.pin.row

import androidx.compose.runtime.Composable
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.util.MutableCoordinate

interface PinRowRepresentation {

    @Composable
    fun onDraw(
        nodeState: NodeState,
        pinRowState: PinRowState,
        pinDragListener: PinDragListener,
        snapshotRequester: SnapshotRequester,
        parentCoordinate: MutableCoordinate
    )

    fun onHoverStart()

    fun onHoverEnd()

}