package com.rodev.jbpkmp.presentation.components.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowState

class NodeState(
    val id: String,
    val nodeRepresentation: NodeRepresentation,
    initialX: Float = 0f,
    initialY: Float = 0f,
) {
    val inputPins = mutableStateListOf<PinRowState>()
    val outputPins = mutableStateListOf<PinRowState>()

    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)
}