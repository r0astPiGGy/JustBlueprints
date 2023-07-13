package com.rodev.jbpkmp.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.theme.AppTheme
import kotlin.math.max
import kotlin.math.roundToInt

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = true) {
            Surface {
                ViewPort(
                    modifier = Modifier
                        .fillMaxSize(),
                    viewPortModifier = Modifier
//                        .drawWithCache {
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
                ) {
                    Node()
                }
//                Box(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    Node()
//                    Node()
//                }
            }
        }
    }
}

private const val nodeOutlinePadding = 8

@Composable
@Preview
fun Node() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .wrapContentSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        NodeBody(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Blue)
                    .padding(nodeOutlinePadding.dp)
            ) {
                Text(
                    text = "Header",
                    modifier = Modifier
                )
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Yellow)
                    .padding(nodeOutlinePadding.dp)
            ) {

            }

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.Yellow)
                    .padding(nodeOutlinePadding.dp)
            ) {

            }
        }
    }

}

@Composable
fun NodeBody(modifier: Modifier, content: @Composable () -> Unit) {
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
            header.placeRelative(0, 0)
            inputContainer.placeRelative(0, header.height)
            outputContainer.placeRelative(inputContainer.width + spaceBetweenContainers, header.height)
        }
    }
}

@Composable
fun InputPin() {

}

@Composable
fun OutputPin() {

}