package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import com.rodev.generator.action.entity.extra_data.ExtraData
import com.rodev.jbpkmp.domain.model.PinEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.createPinTag
import com.rodev.nodeui.components.pin.PinDisplay
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.model.Pin

class DefaultPinDisplay(
    private val pinEntity: PinEntity,
    val extraData: ExtraData? = null
) : PinDisplay {

    override val name: String
        get() = pinEntity.name

    override val color: Int
        get() = pinEntity.color

    override val type: Any
        get() = pinEntity.type

    override fun toPin(pinState: PinState): Pin {
        require(pinState.pinDisplay == this)

        return Pin(
            uniqueId = pinState.id,
            createPinTag(
                typeId = pinEntity.id,
                value = pinState.defaultValueComposable.getValue()
            )
        )
    }

}

val PinDisplay.extra: ExtraData?
    get() = (this as? DefaultPinDisplay)?.extraData