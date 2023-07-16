package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowState
import com.rodev.jbpkmp.presentation.components.wire.PinWire
import com.rodev.jbpkmp.util.MutableCoordinate

class PinState(
    val parent: PinRowState,
    val entity: PinEntity,
    val defaultValueComposable: DefaultValueComposable = EmptyDefaultValueComposable,
    val drawFunction: PinDrawFunction = defaultDrawFunction()
) {
    val connections = mutableListOf<PinWire>()
    val position: MutableCoordinate = MutableCoordinate()
    val center: MutableCoordinate = MutableCoordinate()

    var connected by mutableStateOf(false)
}

fun PinState.getNode() = parent.node

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