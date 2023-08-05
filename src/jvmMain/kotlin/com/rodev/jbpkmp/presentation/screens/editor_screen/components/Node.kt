package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.rodev.generator.action.entity.PinType
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultDrawFunction
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.ExecDrawFunction
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.pin.pinDragModifier
import com.rodev.nodeui.components.pin.row.PinRowSnapshot
import com.rodev.nodeui.components.pin.row.PinRowState
import kotlin.math.roundToInt

const val pinSize = 10
const val pinPadding = pinSize / 2
const val boundSpacing = 16
const val boundSpacingWithPin = boundSpacing - pinPadding
const val verticalSpacing = 8

val headerColor = Color(151, 30, 202)
val backgroundColor = Color(60, 58, 54, alpha = 230)
val pinColor = Color(11, 218, 81)
val pinOutline = Color(0, 0, 0)

@Composable
fun InputPinRow(
    nodeState: NodeState,
    absoluteBodyOffset: Offset,
    pinRowState: PinRowState
) {
    var rowOffset by remember { mutableStateOf(Offset.Zero) }
    val updatableBodyOffset by rememberUpdatedState(absoluteBodyOffset)
    val absoluteRowOffset by remember { derivedStateOf {
        rowOffset + updatableBodyOffset
    } }

    var lastRowMeasurement by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth()
            .onGloballyPositioned {
                lastRowMeasurement = it
                rowOffset = it.positionInParent()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
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
                        x = updatableBodyOffset.x + positionInParent.x,
                        y = updatableBodyOffset.y + positionInParent.y
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
        PinComposableRevamped(
            nodeState,
            pinRowState.pinState,
            absoluteRowOffset
        )
        Column(
            modifier = Modifier
                .padding(
                    start = boundSpacingWithPin.dp,
                    end = boundSpacing.dp
                )
                .width(IntrinsicSize.Max)
        ) {
            Text(
                text = pinRowState.pinState.pinDisplay.name,
                color = MaterialTheme.colors.onBackground
            )
            pinRowState.pinState.defaultValueComposable.DefaultValueView(pinRowState.pinState)
        }
    }

}

private val PinState.type: PinType?
    get() = pinDisplay.type as? PinType

private fun PinState.isExec(): Boolean {
    return type?.id == "exec"
}

@Composable
fun OutputPinRow(
    nodeState: NodeState,
    absoluteBodyOffset: Offset,
    pinRowState: PinRowState
) {
    var rowOffset by remember { mutableStateOf(Offset.Zero) }
    val updatableBodyOffset by rememberUpdatedState(absoluteBodyOffset)
    val absoluteRowOffset by remember { derivedStateOf {
        rowOffset + updatableBodyOffset
    } }

    var lastRowMeasurement by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth()
            .onGloballyPositioned {
                lastRowMeasurement = it
                rowOffset = it.positionInParent()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
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
                        x = updatableBodyOffset.x + positionInParent.x,
                        y = updatableBodyOffset.y + positionInParent.y
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

        Column(
            modifier = Modifier
                .padding(
                    start = boundSpacing.dp,
                    end = boundSpacingWithPin.dp
                )
                .width(IntrinsicSize.Min)
        ) {
            Text(
                text = pinRowState.pinState.pinDisplay.name,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            pinRowState.pinState.defaultValueComposable.DefaultValueView(pinRowState.pinState)
        }
        PinComposableRevamped(nodeState, pinRowState.pinState, absoluteRowOffset)
    }
}

@Composable
fun PinComposableRevamped(
    nodeState: NodeState,
    pinState: PinState,
    rowOffset: Offset
) {
    var pinOffset by remember { mutableStateOf(Offset.Zero) }
    var centerInParent by remember { mutableStateOf(Offset.Zero) }

    val updatableRowOffset by rememberUpdatedState(rowOffset)

    val absolutePinPosition by remember { derivedStateOf {
        updatableRowOffset + pinOffset
    } }

    val absolutePinCenter by remember { derivedStateOf {
        absolutePinPosition + centerInParent
    } }

    pinState.position = absolutePinPosition
    pinState.center = absolutePinCenter

    Canvas(
        modifier = Modifier
            .size(pinSize.dp)
            .onGloballyPositioned {
                pinOffset = it.positionInParent()
                centerInParent = Offset(it.size.center.x.toFloat(), it.size.center.y.toFloat())
            }
            .pinDragModifier(nodeState, pinState),
    ) {
        if (pinState.isExec()) {
            ExecDrawFunction.drawPin(this, pinState)
        } else {
            DefaultDrawFunction.drawPin(this, pinState)
        }
    }
}

@Composable
@Preview
fun StyledNode(
    nodeState: NodeState,
    nodeEntity: NodeEntity,
    selected: Boolean,
    onTap: () -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        elevation = 10.dp,
        modifier = Modifier
            .wrapContentSize()
            .offset { IntOffset(
                nodeState.x.roundToInt(),
                nodeState.y.roundToInt()
            ) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    nodeState.x = (dragAmount.x + nodeState.x).coerceAtLeast(0f)
                    nodeState.y = (dragAmount.y + nodeState.y).coerceAtLeast(0f)

                    change.consume()
                }
            }
    ) {
        var bodyOffset by remember { mutableStateOf(Offset.Zero) }
        val absoluteBodyPosition by remember { derivedStateOf {
            bodyOffset + Offset(nodeState.x, nodeState.y)
        } }

        Column(
            modifier = Modifier
                .padding(pinPadding.dp)
                .background(backgroundColor)
                .requiredWidth(IntrinsicSize.Max)
                .onGloballyPositioned {
                    bodyOffset = it.positionInParent()
                }
                .clickable {
                    onTap()
                }
                .drawBehind {
                    if (selected) {
                        drawRect(color = Color.White, style = Stroke(2f))
                    }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(nodeEntity.headerColor))
                    .drawWithCache {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            moveTo(0f, size.height)
                            lineTo(0f, 0f)
                        }
                        onDrawBehind {
                            if (selected) {
                                drawPath(path, color = Color.White, style = Stroke(2f))
                            }
                        }
                    }
                    .padding(start = boundSpacing.dp, end = boundSpacing.dp)
                    .fillMaxWidth()
            ) {
                val image = remember { GlobalDataSource.getIconById(nodeEntity.iconPath) }
                if (image != null) {
                    Image(
                        painter = BitmapPainter(image),
                        modifier = Modifier
                            .size(25.dp),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(nodeEntity.header, color = Color.White)
            }
            NodeSpacer()
            nodeState.outputPins.forEach {
                OutputPinRow(
                    nodeState,
                    absoluteBodyPosition,
                    it
                )
                NodeSpacer()
            }
            nodeState.inputPins.forEach {
                InputPinRow(
                    nodeState,
                    absoluteBodyPosition,
                    it
                )
                NodeSpacer()
            }
        }
    }
}

@Composable
private fun NodeSpacer() {
    Spacer(modifier = Modifier.height(verticalSpacing.dp))
}

fun Modifier.ignorePadding(padding: Int): Modifier {
    return this.layout { measurable, constraints ->
        var offset = 0
        if (constraints.maxWidth != Int.MAX_VALUE) {
            offset = (padding.dp.roundToPx() * 2)
        }

        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + offset
            )
        )

        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

