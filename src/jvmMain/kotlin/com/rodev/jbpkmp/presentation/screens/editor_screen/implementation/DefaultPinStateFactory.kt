package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.jbpkmp.util.randomPinEntity
import com.rodev.nodeui.components.pin.*
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

class DefaultPinStateFactory : PinStateFactory() {

    override fun createInputPinState(pin: Pin): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(pin, ConnectionType.INPUT),
            defaultValueComposable = StringInputComposable().visibleIfNotConnected()
        )
    }

    override fun createPinRepresentation(pin: Pin, connectionType: ConnectionType): PinRepresentation {
        return DefaultPinRepresentation(randomPinEntity(connectionType), connectionType)
    }

}