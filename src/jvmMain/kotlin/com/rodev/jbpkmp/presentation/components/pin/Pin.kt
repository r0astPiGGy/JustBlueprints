package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
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
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester
) {
    PinRow(
        pinState,
        containerPosition,
        pinDragListener,
        snapshotRequester,
        input = true
    ) {
            var inputText by remember { mutableStateOf("") }
            TextField(value = inputText, onValueChange = { inputText = it }, singleLine = false)
    }
}

@Composable
fun OutputPin(
    pinState: PinState,
    containerPosition: MutableCoordinate,
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester
) {
    PinRow(
        pinState,
        containerPosition,
        pinDragListener,
        snapshotRequester,
        input = false
    )
}

@Composable
private fun PinRow(
    pinState: PinState,
    containerPosition: MutableCoordinate,
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester,
    input: Boolean,
    pinContent: @Composable ColumnScope.() -> Unit = {}
) {
    var lastRowMeasurement by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .onGloballyPositioned {
                val positionInParent = it.positionInParent()

                positionInParent.apply {
                    pinState.position.x = containerPosition.x + x
                    pinState.position.y = containerPosition.y + y
                }
                lastRowMeasurement = it
            }
            .alpha(if (pinState.rowHovered) 0.8f else 1f)
    ) {
        if (snapshotRequester.snapshotRequested) {
            val rowMeasurement = lastRowMeasurement!!
            val positionInParent = rowMeasurement.positionInParent()
            val bounds = rowMeasurement.boundsInParent()

            val topBound = Offset(
                x = containerPosition.x + positionInParent.x,
                y = containerPosition.y + positionInParent.y
            )

            // todo: filter if snapshot is outside of the window
            snapshotRequester.addSnapshot(
                PinRowSnapshot(
                    pinState,
                    topBound,
                    topBound.let {
                        Offset(it.x + bounds.width, it.y + bounds.height)
                    }
                )
            )
        }

        if (input) {
            Pin(pinState, pinDragListener)
            Spacer(modifier = Modifier.size(6.dp))
        }

        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
            pinContent()
        }

        if (!input) {
            Spacer(modifier = Modifier.size(6.dp))
            Pin(pinState, pinDragListener)
        }
    }
}

interface PinDragListener {

    fun onPinDragStart(pinState: PinState)

    fun onPinDrag(pinState: PinState, offset: Offset, change: PointerInputChange)

    fun onPinDragEnd()

}

interface SnapshotRequester {

    val snapshotRequested: Boolean

    fun addSnapshot(snapshot: PinRowSnapshot)

}

data class PinRowSnapshot(
    val pinState: PinState,
    val topBound: Offset,
    val bottomBound: Offset
)

@Composable
fun Pin(
    pinState: PinState,
    pinDragListener: PinDragListener
) {
    Canvas(
        modifier = Modifier
            .size(pinSize.dp)
            .onGloballyPositioned {
                it.boundsInParent().center.apply {
                    pinState.center.x = pinState.position.x + x
                    pinState.center.y = pinState.position.y + y
                }

                it.positionInParent().apply {
                    pinState.position.x += x
                    pinState.position.y += y
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        pinDragListener.onPinDragStart(pinState)
                    },
                    onDragEnd = {
                        pinDragListener.onPinDragEnd()
                    }
                ) { change: PointerInputChange, dragAmount: Offset ->
                    pinDragListener.onPinDrag(pinState, dragAmount, change)
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