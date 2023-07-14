package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.util.MutableCoordinate

private const val pinSize = 15

@Composable
fun InputPin(
    pinState: PinState,
    containerPosition: MutableCoordinate,
    pinDragHandler: PinDragHandler
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .onGloballyPositioned {
                it.positionInParent().apply {
                    pinState.position.x = containerPosition.x + x
                    pinState.position.y = containerPosition.y + y
                }
            }
    ) {
        Pin(pinState, pinDragHandler)
        Spacer(modifier = Modifier.size(6.dp))
        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
//            var inputText by remember { mutableStateOf("") }
//            TextField(value = inputText, onValueChange = { inputText = it }, singleLine = true)
        }
    }
}

@Composable
fun OutputPin(
    pinState: PinState,
    containerPosition: MutableCoordinate,
    pinDragHandler: PinDragHandler
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .onGloballyPositioned {
                it.positionInParent().apply {
                    pinState.position.x = containerPosition.x + x
                    pinState.position.y = containerPosition.y + y
                }
            }
    ) {
        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
        Pin(pinState, pinDragHandler)
    }
}

interface PinDragHandler {

    fun onDragStart(pinState: PinState)

    fun onDrag(pinState: PinState, offset: Offset, change: PointerInputChange)

    fun onEnd()

}

@Composable
fun Pin(
    pinState: PinState,
    pinDragHandler: PinDragHandler
) {
    Canvas(
        modifier = Modifier
            .size(pinSize.dp)
            .onGloballyPositioned {
                it.boundsInParent().center.apply {
                    pinState.position.x += x
                    pinState.position.y += y
                }
                println("[${pinState.entity.name}] position in layout = ${pinState.position.x}, ${pinState.position.y}")
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        pinDragHandler.onDragStart(pinState)
                    },
                    onDragEnd = {
                        pinDragHandler.onEnd()
                    }
                ) { change: PointerInputChange, dragAmount: Offset ->
                    pinDragHandler.onDrag(pinState, dragAmount, change)
                    change.consume()
                }
            }
    ) {
        drawCircle(
            color = Color(pinState.entity.color),
            style = if (pinState.connected) Fill else Stroke(width = 2f)
        )
    }
}