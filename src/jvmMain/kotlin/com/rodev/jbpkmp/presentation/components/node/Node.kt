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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.presentation.components.ViewPort
import com.rodev.jbpkmp.presentation.components.pin.InputPin
import com.rodev.jbpkmp.presentation.components.pin.OutputPin
import com.rodev.jbpkmp.presentation.components.pin.PinState
import com.rodev.jbpkmp.presentation.viewmodel.ViewPortViewModel
import com.rodev.jbpkmp.theme.AppTheme
import kotlin.math.max
import kotlin.math.roundToInt

fun main() = application {
    val viewPortModel = remember { ViewPortViewModel() }

    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = true) {
            Surface {
                ViewPort(
                    modifier = Modifier
                        .fillMaxSize(),
                    viewPortModifier = Modifier,
                    viewModel = viewPortModel
                ) {
                    nodeStates.forEach {
                        // TODO нужен remember {} для state ?
                        Node(it)
                    }
                }
                Button(onClick = {
                    viewPortModel.onNodeAdd()
                }) {
                    Text(text = "Add node")
                }
            }
        }
    }
}

private const val nodeOutlinePadding = 6

//    .drawWithCache {
//                            onDrawWithContent {
//
//                            }
//                            onDrawBehind {
//                                val path = Path()
//                                path.moveTo(0f, 0f)
//                                path.cubicTo(x1 = 333.dp.toPx()/10, y1 = 0f, x2 = 333.dp.toPx()/4, y2 = 70.dp.toPx(), x3 = 333.dp.toPx()/2, y3 = 70.dp.toPx())
//                                path.cubicTo(x1 = 333.dp.toPx()*3/4, y1 = 70.dp.toPx(), x2 = 333.dp.toPx()-333.dp.toPx()/10, y2 = 0f, x3 = 333.dp.toPx(), y3 = 0f)
//                                drawPath(
//                                    path = path,
//                                    color = Color.Black,
//                                    style = Stroke()
//                                )
//                            }
//                        }

@Composable
@Preview
fun Node(
    nodeState: NodeState
) {
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
    ) {
        NodeBody(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .defaultMinSize(minWidth = 100.dp)
                .wrapContentHeight()
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

            PinContainer(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopStart)
            ) {
                nodeState.nodeEntity.inputPins.forEach {
                    // TODO нужен remember {} для state ?
                    InputPin(PinState(it))
                }
            }

            PinContainer (
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopEnd)
            ) {
                nodeState.nodeEntity.outputPins.forEach {
                    // TODO нужен remember {} для state ?
                    OutputPin(PinState(it))
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
        modifier = modifier
            .defaultMinSize(minWidth = 50.dp, minHeight = 50.dp)
            .padding(nodeOutlinePadding.dp)
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
