package com.rodev.jbpkmp.presentation.components.graph

import com.rodev.jbpkmp.presentation.components.pin.PinState

interface PinTypeComparator {

    fun connectable(inputPin: PinState, outputPin: PinState): Boolean

    object Default : PinTypeComparator {
        override fun connectable(inputPin: PinState, outputPin: PinState) = true
    }

}