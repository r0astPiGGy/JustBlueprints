package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.PinEntity

class PinState(
    val entity: PinEntity
) {
    var connected by mutableStateOf(false)
}