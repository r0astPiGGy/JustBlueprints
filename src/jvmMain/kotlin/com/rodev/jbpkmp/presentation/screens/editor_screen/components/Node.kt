package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*
import com.rodev.generator.action.entity.PinType
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinShape
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.ExecPinShape
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

@OptIn(ExperimentalComposeUiApi::class)
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

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth()
            .onGloballyPositioned {
                lastRowMeasurement = it
                rowOffset = it.positionInParent()
            }
            .pinDragModifier(nodeState, pinRowState.pinState) {
                absoluteRowOffset
            }
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIconDefaults.Crosshair),
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
            pinRowState.pinState,
            absoluteRowOffset,
            interactionSource
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

@OptIn(ExperimentalComposeUiApi::class)
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

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth()
            .onGloballyPositioned {
                lastRowMeasurement = it
                rowOffset = it.positionInParent()
            }
            .pinDragModifier(nodeState, pinRowState.pinState) {
                absoluteRowOffset
            }
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIconDefaults.Crosshair),
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
        PinComposableRevamped(pinRowState.pinState, absoluteRowOffset, interactionSource)
    }
}

@Composable
fun PinComposableRevamped(
    pinState: PinState,
    rowOffset: Offset,
    interactionSource: MutableInteractionSource
) {
    var pinOffset by remember { mutableStateOf(Offset.Zero) }
    var centerInParent by remember { mutableStateOf(Offset.Zero) }

    val updatableRowOffset by rememberUpdatedState(rowOffset)

    val absolutePinCenter by remember { derivedStateOf {
        updatableRowOffset + pinOffset + centerInParent
    } }

    // TODO remove Side effect
    pinState.center = absolutePinCenter

    val shape = remember {
        if (pinState.isExec()) {
            ExecPinShape
        } else {
            DefaultPinShape
        }
    }

    Canvas(
        modifier = Modifier
            .size(pinSize.dp)
            .onGloballyPositioned {
                pinOffset = it.positionInParent()
                centerInParent = Offset(it.size.center.x.toFloat(), it.size.center.y.toFloat())
            }
            .clip(shape)
            .indication(interactionSource, PinHoverIndication)
    ) {
        drawRect(color = Color(pinState.pinDisplay.color))
    }
}

private object PinHoverIndication : Indication {

    private class PinHoverIndicationInstance(
        private val isHovered: State<Boolean>
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isHovered.value) {
                drawRect(color = Color.White.copy(alpha = 0.2f), size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isHovered = interactionSource.collectIsHoveredAsState()
        return remember(interactionSource) {
            PinHoverIndicationInstance(isHovered)
        }
    }
}

@Composable
private fun DoubleExecPinRow(
    nodeState: NodeState,
    inputPinRowState: PinRowState,
    outputPinRowState: PinRowState,
    absoluteBodyOffset: Offset
) {
    val updatableBodyOffset by rememberUpdatedState(absoluteBodyOffset)
    var offset by remember { mutableStateOf(Offset.Zero) }

    val bodyOffset by remember { derivedStateOf {
        offset + updatableBodyOffset
    } }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                offset = it.positionInParent()
            }
    ) {
        Box(
            modifier = Modifier.requiredWidth(100.dp)
        ) {
            InputPinRow(
                nodeState = nodeState,
                absoluteBodyOffset = bodyOffset,
                pinRowState = inputPinRowState
            )
        }

        var outputBoxOffset by remember { mutableStateOf(Offset.Zero) }

        val boxOffset by remember { derivedStateOf {
            outputBoxOffset + bodyOffset
        } }

        Box(
            modifier = Modifier
                .requiredWidth(100.dp)
                .onGloballyPositioned {
                    outputBoxOffset = it.positionInParent()
                }
        ) {
            OutputPinRow(
                nodeState = nodeState,
                absoluteBodyOffset = boxOffset,
                pinRowState = outputPinRowState
            )
        }
    }
}

@Composable
private fun DisabledConnectionRow(
    pinRowState: PinRowState
) {
    Column(
        modifier = Modifier
            .padding(
                start = boundSpacing.dp,
                end = boundSpacing.dp
            )
    ) {
        Text(
            text = pinRowState.pinState.pinDisplay.name,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        pinRowState.pinState.defaultValueComposable.DefaultValueView(pinRowState.pinState)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun StyledNode(
    specificNodePins: SpecificNodePins,
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

        // CompositionLocal candidate
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onTap()
                }
                .pointerHoverIcon(PointerIconDefaults.Hand)
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

            specificNodePins.execPinPair?.let {
                DoubleExecPinRow(
                    nodeState = nodeState,
                    inputPinRowState = it.input,
                    outputPinRowState = it.output,
                    absoluteBodyOffset = absoluteBodyPosition
                )
                NodeSpacer()
            }
            specificNodePins.outputPins.forEach {
                OutputPinRow(
                    nodeState = nodeState,
                    pinRowState = it,
                    absoluteBodyOffset = absoluteBodyPosition
                )
                NodeSpacer()
            }
            specificNodePins.inputPins.forEach {
                InputPinRow(
                    nodeState = nodeState,
                    pinRowState = it,
                    absoluteBodyOffset = absoluteBodyPosition
                )
                NodeSpacer()
            }
            specificNodePins.connectionDisabledInputPins.forEach {
                DisabledConnectionRow(
                    pinRowState = it
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

