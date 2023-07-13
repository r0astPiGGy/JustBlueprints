package com.rodev.jbpkmp.presentation.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.theme.AppTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = true) {
            Surface {
//                ViewPort(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    viewPortModifier = Modifier
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
//                ) {
//                    Node()
//                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Node()
                }
            }
        }
    }
}

@Composable
@Preview
fun Node() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val outlinePadding = 8.dp

//    Card(
//        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier
////            .size(width = 250.dp, 140.dp)
//            .absoluteOffset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
//            .wrapContentSize()
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    change.consume()
//                    offsetX += dragAmount.x
//                    offsetY += dragAmount.y
//                }
//            }
//    ) {
//        Column(
//            modifier = Modifier
//                .wrapContentSize()
//        ) {
//            // Node Header
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(40.dp)
//                    .background(Color.Blue)
//                    .padding(outlinePadding)
//            ) {
//                Text(
//                    text = "Header",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .padding(outlinePadding),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Input Pins
//                Column(
//                    modifier = Modifier
//                        .requiredSize(300.dp)
//                        .background(Color.Yellow)
//                ) {
//
//                }
//                // Output Pins
//                Column(
//                    modifier = Modifier
//                        .requiredSize(400.dp)
//                        .background(Color.Red)
//                ) {
//
//                }
//            }
//        }
//    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Row(
            modifier = Modifier
                .background(Color.Blue)
        ) {
            Icon(Icons.Outlined.Home, contentDescription = null)

            Text("Headeафутафуафуаирфуаирфуаирфуаифруаифруаирфуr")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Yellow)
            )

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.Yellow)
            )
        }
    }
}

@Composable
fun InputPin() {

}

@Composable
fun OutputPin() {

}