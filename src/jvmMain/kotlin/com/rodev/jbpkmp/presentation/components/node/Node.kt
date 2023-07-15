package com.rodev.jbpkmp.presentation.components.node

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.presentation.components.graph.GraphViewModel
import com.rodev.jbpkmp.presentation.components.graph.GraphViewPort
import com.rodev.jbpkmp.presentation.components.graph.NodeAddEvent
import com.rodev.jbpkmp.presentation.components.graph.NodeClearEvent
import com.rodev.jbpkmp.presentation.components.pin.*
import com.rodev.jbpkmp.theme.AppTheme
import com.rodev.jbpkmp.util.MutableCoordinate
import com.rodev.jbpkmp.util.randomNode
import kotlin.math.max
import kotlin.math.roundToInt

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = true) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ViewPortPreview()
                }
            }
        }
    }
}

@Composable
private fun ViewPortPreview() {
    val viewPortModel = remember { GraphViewModel() }

    Row {
        Button(onClick = {
            viewPortModel.onEvent(NodeAddEvent(randomNode()))
        }) {
            Text(text = "Add node")
        }
        Button(onClick = {
            viewPortModel.onEvent(NodeClearEvent)
        }) {
            Text(text = "Clear")
        }
    }

    GraphViewPort(
        modifier = Modifier
            .fillMaxSize(),
        graphModifier = Modifier
            .drawBehind {
                viewPortModel.temporaryLine.value?.drawFunction()?.invoke(this)
                viewPortModel.lines.forEach { it.drawFunction().invoke(this) }
            },
        viewModel = viewPortModel,
    ) {
        nodeStates.forEach {
            Node(it, viewPortModel, viewPortModel)
        }
    }
}

private const val nodeOutlinePadding = 6

@Composable
@Preview
fun Node(
    nodeState: NodeState,
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester
) {
    val nodeBodyRelativeCoordinates = remember {
        MutableCoordinate()
    }
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .offset { IntOffset(
                nodeState.x.roundToInt().coerceAtLeast(0),
                nodeState.y.roundToInt().coerceAtLeast(0)
            ) }
            .wrapContentSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    nodeState.x += dragAmount.x
                    nodeState.y += dragAmount.y
                }
            }
            .onGloballyPositioned {
                it.positionInParent().apply {
                    nodeBodyRelativeCoordinates.x = x
                    nodeBodyRelativeCoordinates.y = y
                }
            }
    ) {
        NodeBody(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .defaultMinSize(minWidth = 100.dp)
                .wrapContentHeight()
                .onGloballyPositioned {
                    it.positionInParent().apply {
                        nodeBodyRelativeCoordinates.x += x
                        nodeBodyRelativeCoordinates.y += y
                    }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .background(
                        Color(nodeState.nodeEntity.headerColor)
                    )
                    .padding(nodeOutlinePadding.dp)
                    .padding(end = 50.dp)
            ) {
                Text(
                    text = nodeState.nodeEntity.header,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            val inputPinContainerCoordinates = remember { MutableCoordinate() }
            val outputPinContainerCoordinates = remember { MutableCoordinate() }

            PinContainer(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopStart)
                    .onGloballyPositioned {
                        it.positionInParent().apply {
                            inputPinContainerCoordinates.x = nodeBodyRelativeCoordinates.x + x
                            inputPinContainerCoordinates.y = nodeBodyRelativeCoordinates.y + y
                        }
                    }
            ) {
                nodeState.nodeEntity.inputPins.forEach {
                    InputPin(
                        remember { PinState(nodeState, it) },
                        inputPinContainerCoordinates,
                        pinDragListener,
                        snapshotRequester
                    )
                }
            }

            PinContainer(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopEnd)
                    .onGloballyPositioned {
                        it.positionInParent().apply {
                            outputPinContainerCoordinates.x = nodeBodyRelativeCoordinates.x + x
                            outputPinContainerCoordinates.y = nodeBodyRelativeCoordinates.y + y
                        }
                    }
            ) {
                nodeState.nodeEntity.outputPins.forEach {
                    OutputPin(
                        remember { PinState(nodeState, it) },
                        outputPinContainerCoordinates,
                        pinDragListener,
                        snapshotRequester
                    )
                }
            }
        }
    }
}

@Composable
private fun PinContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column (
        content = content,
        modifier = Modifier
            .defaultMinSize(minWidth = 50.dp, minHeight = 50.dp)
            .padding(nodeOutlinePadding.dp)
            .then(modifier)
    )
}

@Composable
private fun NodeBody(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = nodeBodyMeasurePolicy()
    )
}

@Composable
private fun nodeBodyMeasurePolicy(): MeasurePolicy = remember {
    MeasurePolicy { measurables, constraints ->
        val inputContainer = measurables[1].measure(constraints)
        val outputContainer = measurables[2].measure(constraints)

        val containersWidth = inputContainer.width + outputContainer.width

        val header = measurables[0].measure(constraints.copy(minWidth = containersWidth))

        val spaceBetweenContainers = max(0, header.width - containersWidth)
        val height = header.height + max(inputContainer.height, outputContainer.height)
        val width = max(header.width, containersWidth)

        layout(width = width, height = height) {
            header.place(0, 0)
            inputContainer.place(0, header.height)
            outputContainer.place(inputContainer.width + spaceBetweenContainers, header.height)
        }
    }
}
