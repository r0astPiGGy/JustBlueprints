package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.geometry.Offset
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
            brush = Brush.linearGradient(
                listOf(Color(inputPin.entity.color), Color(outputPin.entity.color)),
                start = Offset(inputPin.center.x, inputPin.center.y),
                end = Offset(outputPin.center.x, outputPin.center.y)
            ),
            style = Stroke(width = Wire.STROKE_WIDTH),
        )
    }
}