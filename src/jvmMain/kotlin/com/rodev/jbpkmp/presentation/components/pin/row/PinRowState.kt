package com.rodev.jbpkmp.presentation.components.pin.row

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.presentation.components.node.NodeState

class PinRowState(
    val node: NodeState
) {
    var hovered by mutableStateOf(false)
}