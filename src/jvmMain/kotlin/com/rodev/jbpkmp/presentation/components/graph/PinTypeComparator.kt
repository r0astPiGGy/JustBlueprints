package com.rodev.jbpkmp.presentation.components.graph

import com.rodev.jbpkmp.presentation.components.pin.PinState

object PinTypeComparator {

    fun connectable(input: PinState, output: PinState): Boolean {
        return true
    }

}