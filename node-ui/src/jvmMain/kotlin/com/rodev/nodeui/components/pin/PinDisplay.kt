package com.rodev.nodeui.components.pin

import com.rodev.nodeui.model.Pin

interface PinDisplay {

    val name: String

    val color: Int

    val type: Any?

    fun toPin(pinState: PinState): Pin

}