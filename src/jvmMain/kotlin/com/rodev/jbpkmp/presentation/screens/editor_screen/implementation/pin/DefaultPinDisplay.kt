package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import com.rodev.generator.action.entity.PinType
import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jbpkmp.presentation.screens.editor_screen.createPinTag
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.Pin

class DefaultPinDisplay(
    private val typeId: String,
    override val name: String,
    override val type: PinType,
    override val color: Int,
    val extraData: ExtraData? = null
) : PinDisplay {

    override fun toPin(pinState: PinState): Pin {
        require(pinState.pinDisplay == this)

        return Pin(
            uniqueId = pinState.id,
            createPinTag(
                typeId = typeId,
                value = pinState.defaultValueComposable.getValue()
            )
        )
    }

}

val PinDisplay.extra: ExtraData?
    get() = (this as? DefaultPinDisplay)?.extraData