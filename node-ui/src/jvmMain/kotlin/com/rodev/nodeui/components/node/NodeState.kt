package com.rodev.nodeui.components.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.nodeui.components.pin.row.PinRowState
import java.util.*

class NodeState(
    val id: String,
    val nodeRepresentation: NodeRepresentation,
    initialX: Float = 0f,
    initialY: Float = 0f,
) {
    val runtimeUUID = UUID.randomUUID().toString()

    val inputPins = mutableStateListOf<PinRowState>()
    val outputPins = mutableStateListOf<PinRowState>()

    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)
}