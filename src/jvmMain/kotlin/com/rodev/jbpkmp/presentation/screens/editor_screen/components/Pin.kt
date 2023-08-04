package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.*
import com.rodev.nodeui.components.pin.row.PinRowSnapshot
import com.rodev.nodeui.components.pin.row.PinRowState

private const val pinSize = 20

@Composable
fun PinRow(
    nodeState: NodeState,
    pinRowState: PinRowState,
    containerPosition: Offset
) {
    var lastRowMeasurement by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .onGloballyPositioned {
                pinRowState.pinState.position = containerPosition + it.positionInParent()

                lastRowMeasurement = it
            }
            .alpha(if (pinRowState.hovered) 0.8f else 1f)
    ) {
        if (nodeState.snapshotRequested) {
            // todo: filter if snapshot is outside the window
            nodeState.addSnapshot(
                PinRowSnapshot.lazy(
                    pinRowState = pinRowState,
                    nodeState = nodeState
                ) {
                    val rowMeasurement = lastRowMeasurement!!
                    val positionInParent = rowMeasurement.positionInParent()
                    val bounds = rowMeasurement.boundsInParent()

                    val topBound = Offset(
                        x = containerPosition.x + positionInParent.x,
                        y = containerPosition.y + positionInParent.y
                    )

                    PinRowSnapshot(
                        nodeState,
                        pinRowState,
                        topBound,
                        topBound.let {
                            Offset(it.x + bounds.width, it.y + bounds.height)
                        }
                    )
                }
            )
        }

        if (pinRowState.pinState.isInput()) {
            pinRowState.pinState.pinDisplay.PinView(nodeState, pinRowState.pinState)
            Spacer(modifier = Modifier.size(6.dp))
        }

        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinRowState.pinState.pinDisplay.name,
                color = MaterialTheme.colors.onBackground
            )
            pinRowState.pinState.defaultValueComposable.DefaultValueView(pinRowState.pinState)
        }

        if (pinRowState.pinState.isOutput()) {
            Spacer(modifier = Modifier.size(6.dp))
            pinRowState.pinState.pinDisplay.PinView(nodeState, pinRowState.pinState)
        }
    }
}

@Composable
fun Pin(
    nodeState: NodeState,
    pinState: PinState,
    onDraw: DrawScope.() -> Unit
) {
    Canvas(
        modifier = Modifier
            .size(pinSize.dp)
            .onGloballyPositioned {
                pinState.center = pinState.position + it.boundsInParent().center
                pinState.position += it.positionInParent()
            }
            .pinDragModifier(nodeState, pinState),
        onDraw = onDraw
    )
}