package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.presentation.components.pin.PinState

data class PinWire(
    val inputPin: PinState,
    val outputPin: PinState
): Wire {
    override fun drawFunction(): DrawScope.() -> Unit = {
        val path = getLinePath(
            inputPin.center.x,
            inputPin.center.y,
            outputPin.center.x,
            outputPin.center.y
        )

        drawPath(
            path = path,
            // TODO add gradient
            brush = Brush.linearGradient(0.5f to Color(inputPin.entity.color), 1f to Color(outputPin.entity.color)),
            style = Stroke(width = Wire.STROKE_WIDTH),
        )
    }
}