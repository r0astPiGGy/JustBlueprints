package com.rodev.nodeui.components.graph

import com.rodev.nodeui.components.pin.PinState

interface PinTypeComparator {

    fun connectable(inputPin: PinState, outputPin: PinState): Boolean

    object Default : PinTypeComparator {
        override fun connectable(inputPin: PinState, outputPin: PinState): Boolean {
            return inputPin.pinRepresentation.type == outputPin.pinRepresentation.type
        }
    }

}