package com.rodev.nodeui.components.node

import androidx.compose.runtime.Composable
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.model.Node

interface NodeRepresentation {

    @Composable
    fun onDraw(nodeState: NodeState, pinDragListener: PinDragListener, snapshotRequester: SnapshotRequester)

    fun toNode(nodeState: NodeState): Node

}