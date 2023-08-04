package com.rodev.nodeui.components.wire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.nodeui.components.pin.PinState
import java.util.*

private data class DefaultPinWire(
    override val inputPin: PinState,
    override val outputPin: PinState
): PinWire() {

    val uuid: UUID = UUID.randomUUID()

    override fun DrawScope.drawFunction() {
        val path = getLinePath(
            inputPin.center.x,
            inputPin.center.y,
            outputPin.center.x,
            outputPin.center.y
        )

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                listOf(Color(inputPin.pinDisplay.color), Color(outputPin.pinDisplay.color)),
                start = Offset(inputPin.center.x, inputPin.center.y),
                end = Offset(outputPin.center.x, outputPin.center.y)
            ),
            style = Stroke(width = STROKE_WIDTH),
        )
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultPinWire

        return uuid == other.uuid
    }
}

abstract class PinWire : Wire() {

    abstract val inputPin: PinState

    abstract val outputPin: PinState

    companion object {

        fun default(inputPin: PinState, outputPin: PinState): PinWire {
            return DefaultPinWire(inputPin, outputPin)
        }

    }
}

fun PinWire.getOpposite(pinState: PinState): PinState {
    return if (inputPin == pinState) outputPin else inputPin
}