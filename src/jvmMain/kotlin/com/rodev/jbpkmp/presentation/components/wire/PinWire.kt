package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.presentation.components.node.NodeState
import com.rodev.jbpkmp.presentation.components.pin.PinState
import com.rodev.jbpkmp.presentation.components.pin.getNode
import java.util.*

data class PinWire(
    val uuid: UUID = UUID.randomUUID(),
    val inputPin: PinState,
    val outputPin: PinState
): Wire() {
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
                listOf(Color(inputPin.entity.color), Color(outputPin.entity.color)),
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

        other as PinWire

        return uuid == other.uuid
    }
}

fun PinWire.getOpposite(pinState: PinState): PinState {
    return if (inputPin == pinState) outputPin else inputPin
}

fun PinWire.getPin(owner: NodeState): PinState? {
    return when (owner) {
        inputPin.getNode() -> inputPin
        outputPin.getNode() -> outputPin
        else -> null
    }
}