package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.util.MutableCoordinate

class PinState(
    val entity: PinEntity,
    val position: MutableCoordinate = MutableCoordinate(),
    val center: MutableCoordinate = MutableCoordinate()
) {
    var connected by mutableStateOf(false)
}