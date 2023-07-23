package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.pin.PinState

object DefaultPinTypeComparator : PinTypeComparator {

    override fun connectable(inputPin: PinState, outputPin: PinState): Boolean {
        return inputPin.pinRepresentation.type == outputPin.pinRepresentation.type
    }

}