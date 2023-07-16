package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowSnapshot
import com.rodev.jbpkmp.presentation.components.pin.row.PinRowState
import com.rodev.jbpkmp.presentation.components.pin.row.SnapshotRequester
import com.rodev.jbpkmp.util.MutableCoordinate

private const val pinSize = 15

@Composable
fun PinRow(
    pinRowState: PinRowState,
    pinState: PinState,
    containerPosition: MutableCoordinate,
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester
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
            .alpha(if (pinRowState.hovered) 0.8f else 1f)
    ) {
        if (snapshotRequester.snapshotRequested) {
            // todo: filter if snapshot is outside the window
            snapshotRequester.addSnapshot(
                PinRowSnapshot.lazy(
                    pinRowState = pinRowState,
                    pinState = pinState
                ) {
                    val rowMeasurement = lastRowMeasurement!!
                    val positionInParent = rowMeasurement.positionInParent()
                    val bounds = rowMeasurement.boundsInParent()

                    val topBound = Offset(
                        x = containerPosition.x + positionInParent.x,
                        y = containerPosition.y + positionInParent.y
                    )

                    PinRowSnapshot(
                        pinRowState,
                        pinState,
                        topBound,
                        topBound.let {
                            Offset(it.x + bounds.width, it.y + bounds.height)
                        }
                    )
                }
            )
        }

        if (pinState.isInput()) {
            Pin(pinState, pinDragListener)
            Spacer(modifier = Modifier.size(6.dp))
        }

        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
            pinState.defaultValueComposable.draw(pinState)
        }

        if (pinState.isOutput()) {
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
        pinState.drawFunction.draw(this, pinState)
    }
}