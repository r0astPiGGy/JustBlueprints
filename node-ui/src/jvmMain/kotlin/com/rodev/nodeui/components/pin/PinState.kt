package com.rodev.nodeui.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.rodev.nodeui.components.wire.PinWire
import com.rodev.nodeui.model.ConnectionType

class PinState(
    val id: String,
    val connectionType: ConnectionType,
    val supportsMultipleConnection: Boolean,
    val pinDisplay: PinDisplay,
    val defaultValueComposable: DefaultValueComposable = EmptyDefaultValueComposable,
) {
    val connections = mutableListOf<PinWire>()
    var position by mutableStateOf(Offset.Zero)
    var center by mutableStateOf(Offset.Zero)

    var connected by mutableStateOf(false)
}

fun PinState.isInput(): Boolean {
    return connectionType == ConnectionType.INPUT
}

fun PinState.isOutput(): Boolean {
    return connectionType == ConnectionType.OUTPUT
}

fun PinState.connectionTypeEquals(pinState: PinState): Boolean {
    return connectionType == pinState.connectionType
}

fun PinState.connectionTypeNotEquals(pinState: PinState) = !connectionTypeEquals(pinState)