package com.rodev.jbpkmp.presentation.components.node

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.components.pin.PinDragListener
import com.rodev.jbpkmp.presentation.components.pin.row.SnapshotRequester

interface NodeRepresentation {

    @Composable
    fun onDraw(nodeState: NodeState, pinDragListener: PinDragListener, snapshotRequester: SnapshotRequester)

}