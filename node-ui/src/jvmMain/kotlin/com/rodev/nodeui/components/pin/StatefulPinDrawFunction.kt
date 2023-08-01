package com.rodev.nodeui.components.pin

import androidx.compose.ui.graphics.drawscope.DrawScope

open class StatefulPinDrawFunction(
    private val connectedDrawFunction: PinDrawFunction,
    private val notConnectedDrawFunction: PinDrawFunction
) : PinDrawFunction {
    final override fun DrawScope.onDraw(pinState: PinState) {
        if (pinState.connected) {
            connectedDrawFunction.drawPin(this, pinState)
        } else {
            notConnectedDrawFunction.drawPin(this, pinState)
        }
    }
}