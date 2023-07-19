package com.rodev.nodeui.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MutableCoordinate(
    initialX: Float = 0f,
    initialY: Float = 0f,
) {
    var x by mutableStateOf(initialX)
    var y by mutableStateOf(initialY)

    override fun toString(): String {
        return "[$x, $y]"
    }
}