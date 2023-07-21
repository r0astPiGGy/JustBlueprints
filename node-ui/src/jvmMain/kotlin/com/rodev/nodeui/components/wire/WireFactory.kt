package com.rodev.nodeui.components.wire

import androidx.compose.ui.geometry.Offset
import com.rodev.nodeui.components.pin.PinState

interface WireFactory {

    fun createPinWire(inputPin: PinState, outputPin: PinState): PinWire

    fun createTemporaryWire(color: Int, start: Offset, end: Offset): Wire

    fun createWirePreview(colorStart: Int, colorEnd: Int, startPos: Offset, endPos: Offset): Wire

}

fun WireFactory(): WireFactory {
    return DefaultWireFactory()
}

private class DefaultWireFactory : WireFactory {
    override fun createPinWire(inputPin: PinState, outputPin: PinState): PinWire {
        return PinWire.default(
            inputPin = inputPin,
            outputPin = outputPin
        )
    }

    override fun createTemporaryWire(color: Int, start: Offset, end: Offset): Wire {
        return TemporaryWire(
            color = color,
            startX = start.x,
            startY = start.y,
            endX = end.x,
            endY = end.y
        )
    }

    override fun createWirePreview(colorStart: Int, colorEnd: Int, startPos: Offset, endPos: Offset): Wire {
        return WirePreview(
            color = colorStart,
            colorEnd = colorEnd,
            startX = startPos.x,
            startY = startPos.y,
            endX = endPos.x,
            endY = endPos.y
        )
    }

}