package com.rodev.nodeui.components.pin

import androidx.compose.ui.graphics.drawscope.DrawScope

interface PinDrawFunction {

    fun drawPin(drawScope: DrawScope, pinState: PinState) {
        with(drawScope) {
            this.onDraw(pinState)
        }
    }

    fun DrawScope.onDraw(pinState: PinState)

}