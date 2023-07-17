package com.rodev.jbpkmp.presentation.components.pin.row

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.presentation.components.pin.PinState

class PinRowState(
    val pinRowRepresentation: PinRowRepresentation,
    val pinState: PinState
) {
    var hovered by mutableStateOf(false)
}