package com.rodev.jbpkmp.presentation.components.pin

import com.rodev.jbpkmp.data.ConnectionType
import com.rodev.jbpkmp.data.PinEntity
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowState

abstract class PinStateFactory {

    fun createPinState(pinRowState: PinRowState, pinEntity: PinEntity): PinState {
        return when (pinEntity.connectionType) {
            ConnectionType.INPUT -> createInputPinState(pinRowState, pinEntity)
            ConnectionType.OUTPUT -> createOutputPinState(pinRowState, pinEntity)
        }
    }

    protected abstract fun createInputPinState(pinRowState: PinRowState, pinEntity: PinEntity): PinState

    protected abstract fun createOutputPinState(pinRowState: PinRowState, pinEntity: PinEntity): PinState

}

object DefaultPinStateFactory : PinStateFactory() {
    override fun createInputPinState(pinRowState: PinRowState, pinEntity: PinEntity): PinState {
        return PinState(pinRowState, pinEntity, StringInputComposable().visibleIfNotConnected())
    }

    override fun createOutputPinState(pinRowState: PinRowState, pinEntity: PinEntity): PinState {
        return PinState(pinRowState, pinEntity)
    }

}