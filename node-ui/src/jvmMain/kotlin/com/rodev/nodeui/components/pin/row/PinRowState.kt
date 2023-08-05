package com.rodev.nodeui.components.pin.row

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.nodeui.components.pin.PinState

class PinRowState(
    val pinState: PinState
) {
    var hovered by mutableStateOf(false)
}