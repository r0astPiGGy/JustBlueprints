package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.StringInputComposable
import com.rodev.jbpkmp.util.randomPinEntity
import com.rodev.nodeui.components.pin.PinRepresentation
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.PinStateFactory
import com.rodev.nodeui.components.pin.visibleIfNotConnected
import com.rodev.nodeui.model.ConnectionType
import com.rodev.nodeui.model.Pin

class DefaultPinStateFactory : PinStateFactory() {

    override fun createInputPinState(pin: Pin): PinState {
        return PinState(
            id = pin.uniqueId,
            pinRepresentation = createPinRepresentation(pin, ConnectionType.INPUT),
            defaultValueComposable = StringInputComposable().visibleIfNotConnected()
        ).apply {
            defaultValueComposable.setValue(pin.value)
        }
    }

    override fun createPinRepresentation(pin: Pin, connectionType: ConnectionType): PinRepresentation {
        return DefaultPinRepresentation(randomPinEntity(connectionType), connectionType)
    }

}