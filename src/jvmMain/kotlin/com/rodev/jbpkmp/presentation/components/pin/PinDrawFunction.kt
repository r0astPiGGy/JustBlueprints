package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

abstract class PinDrawFunction {

    fun draw(drawScope: DrawScope, pinState: PinState) {
        with(drawScope) {
            this.onDraw(pinState)
        }
    }

    protected abstract fun DrawScope.onDraw(pinState: PinState)

}

fun defaultDrawFunction(): PinDrawFunction = DefaultDrawFunction

private object DefaultDrawFunction : PinDrawFunction() {
    override fun DrawScope.onDraw(pinState: PinState) {
        drawCircle(
            color = Color(pinState.pinRepresentation.color),
            style = if (pinState.connected) Fill else Stroke(width = 2f)
        )
    }
}