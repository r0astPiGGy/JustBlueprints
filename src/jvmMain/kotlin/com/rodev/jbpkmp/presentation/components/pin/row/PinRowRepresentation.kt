package com.rodev.jbpkmp.presentation.components.pin.row

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.util.MutableCoordinate

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