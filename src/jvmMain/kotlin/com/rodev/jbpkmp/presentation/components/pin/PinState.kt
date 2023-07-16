package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.presentation.components.wire.PinWire
import com.rodev.jbpkmp.util.MutableCoordinate

class PinState(
    val id: String,
    val pinRepresentation: PinRepresentation,
    val defaultValueComposable: DefaultValueComposable = EmptyDefaultValueComposable,
) {
    val connections = mutableListOf<PinWire>()
    val position: MutableCoordinate = MutableCoordinate()
    val center: MutableCoordinate = MutableCoordinate()

    var connected by mutableStateOf(false)
}

fun PinState.isInput(): Boolean {
    return pinRepresentation.connectionType == ConnectionType.INPUT
}

fun PinState.isOutput(): Boolean {
    return pinRepresentation.connectionType == ConnectionType.OUTPUT
}

fun PinState.connectionTypeEquals(pinState: PinState): Boolean {
    return pinRepresentation.connectionType == pinState.pinRepresentation.connectionType
}

fun PinState.connectionTypeNotEquals(pinState: PinState) = !connectionTypeEquals(pinState)