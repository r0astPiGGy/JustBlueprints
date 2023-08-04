package com.rodev.nodeui.components.pin.row

import androidx.compose.runtime.Composable
import com.rodev.nodeui.components.node.NodeState

interface PinRowDisplay {

    @Composable
    fun PinRowView(
        nodeState: NodeState,
        pinRowState: PinRowState
    )

}