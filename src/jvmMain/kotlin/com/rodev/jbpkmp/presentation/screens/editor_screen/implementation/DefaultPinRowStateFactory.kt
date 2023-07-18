package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.nodeui.components.pin.PinStateFactory
import com.rodev.nodeui.components.pin.row.PinRowRepresentation
import com.rodev.nodeui.components.pin.row.PinRowStateFactory
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

class DefaultPinRowStateFactory(pinStateFactory: PinStateFactory) : PinRowStateFactory(pinStateFactory) {

    override fun createRowRepresentation(pin: Pin, connectionType: ConnectionType): PinRowRepresentation {
        return DefaultPinRowRepresentation
    }

}