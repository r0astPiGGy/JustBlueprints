package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.wire.PinWire
import com.rodev.jbpkmp.util.MutableCoordinate

// todo: PinRowState
class PinState(
    val parent: NodeState,
    val entity: PinEntity,
    val position: MutableCoordinate = MutableCoordinate(),
    val center: MutableCoordinate = MutableCoordinate(),
) {
    val connections = mutableListOf<PinWire>()

    var connected by mutableStateOf(false)
    var rowHovered by mutableStateOf(false)
}

fun PinState.isInput(): Boolean {
    return entity.connectionType == ConnectionType.INPUT
}

fun PinState.isOutput(): Boolean {
    return entity.connectionType == ConnectionType.OUTPUT
}

fun PinState.connectionTypeEquals(pinState: PinState): Boolean {
    return entity.connectionType == pinState.entity.connectionType
}

fun PinState.connectionTypeNotEquals(pinState: PinState) = !connectionTypeEquals(pinState)