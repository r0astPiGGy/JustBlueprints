package com.rodev.jbpkmp

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.singleWindowApplication
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random


private const val pinSize = 10
private const val pinPadding = pinSize / 2
private const val boundSpacing = 16
private const val boundSpacingWithPin = boundSpacing - pinPadding
private const val verticalSpacing = 8

private fun randomText(): String {
    return "b".repeat(Random.nextInt(25))
}


private fun Offset.rotateBy(degrees: Float, from: Offset): Offset {
    val angle = (degrees) * (Math.PI / 180)

    return Offset(
        x = (cos(angle) * (x - from.x) - sin(angle) * (y - from.y) + from.x).toFloat(),
        y = (sin(angle) * (x - from.x) + cos(angle) * (y - from.y) + from.y).toFloat()
    )


}

@Composable
private fun Pin() {
    Canvas(modifier = Modifier.size(pinSize.dp)) {
        drawCircle(color = pinOutline, style = Stroke(width = 2f))
        drawCircle(color = pinColor)
    }
}

@Composable
private fun ExecPin() {
    Canvas(modifier = androidx.compose.ui.Modifier.size(pinSize.dp)) {
        val path = Path().apply {
            val origin = Offset(
                x = center.x,
                y = 0f
            )

            val a = origin.rotateBy(90f, center)
            val b = origin.rotateBy(210f, center)
            val c = origin.rotateBy(330f, center)

            moveTo(a.x, a.y)
            lineTo(b.x, b.y)
            lineTo(c.x, c.y)
            lineTo(a.x, a.y)
        }

        drawPath(path, color = pinOutline, style = Stroke(width = 2f))
        drawPath(path, color = Color.White)
    }
}



@Composable
private fun PinRow() {
    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Pin()
        Text(
            randomText(),
            color = Color.White,
            modifier = Modifier
                .padding(
                    start = boundSpacingWithPin.dp,
                    end = boundSpacing.dp
                )
        )
    }
}


private fun Modifier.startEndPadding(padding: Dp): Modifier {
    return this.then(
        Modifier.padding(start = padding, end = padding)
    )
}

@Composable
private fun PinWithTextRight(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = boundSpacing.dp,
                    end = 40.dp
                )
        ) {
            Text(
                text = "Variable",
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                maxLines = 1,
            )
            Text(
                text = "subHeader",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontSize = 13.sp,
                color = Color.White,
                fontStyle = FontStyle.Italic
            )
        }
        Pin()
    }
}

@Composable
private fun NoPinContainer() {
    Row(
        modifier = Modifier
            .padding(start = boundSpacing.dp, end = boundSpacing.dp)
    ) {
        var text by remember { mutableStateOf("") }
        TextField(value = text, onValueChange = { text = it })
    }
}


@Composable
private fun DoublePinRow() {
    Row(
        modifier = Modifier
            .ignorePadding(pinPadding)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PinWithTextLeft("Input pin")
        PinWithTextRight("Output pin")
    }
}

@Composable
private fun PinWithTextLeft(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExecPin()
        Text(
            text,
            color = Color.White,
            modifier = Modifier
        )
    }
}

@Composable
@Preview
fun VariablePreview() {
    var offset by remember { mutableStateOf(Offset.Zero) }

    Surface(
        color = Color.Transparent,
        elevation = 10.dp,
        modifier = Modifier
            .wrapContentSize()
            .offset { IntOffset(
                offset.x.roundToInt(),
                offset.y.roundToInt()
            ) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset = offset.copy(
                        x = (dragAmount.x + offset.x).coerceAtLeast(0f),
                        y = (dragAmount.y + offset.y).coerceAtLeast(0f),
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .padding(pinPadding.dp)
                .requiredWidth(IntrinsicSize.Max)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .drawBehind {
                        val cornerRadius = CornerRadius(5f)
                        drawRoundRect(color = headerColor, cornerRadius = cornerRadius)
                        drawRoundRect(color = Color.White, cornerRadius = cornerRadius, style = Stroke(2f))
                    }
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .ignorePadding(pinPadding)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = boundSpacing.dp,
                                    end = 40.dp
                                )
                        ) {
                            Text(
                                text = "Variable",
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                                maxLines = 1,
                            )
                            Text(
                                text = "subHeader",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Pin()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun NodePreview() {
    var offset by remember { mutableStateOf(Offset.Zero) }

    Surface(
        color = Color.Transparent,
        elevation = 10.dp,
        modifier = Modifier
            .wrapContentSize()
            .offset { IntOffset(
                offset.x.roundToInt(),
                offset.y.roundToInt()
            ) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offset = offset.copy(
                        x = (dragAmount.x + offset.x).coerceAtLeast(0f),
                        y = (dragAmount.y + offset.y).coerceAtLeast(0f),
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .padding(pinPadding.dp)
                .background(backgroundColor)
                .requiredWidth(IntrinsicSize.Max)
                .drawBehind {
                    drawRect(color = Color.White, style = Stroke(2f))
                }
        ) {
            Row(
                modifier = Modifier
                    .background(headerColor)
                    .drawWithCache {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            moveTo(0f, size.height)
                            lineTo(0f, 0f)
                        }
                        onDrawBehind {
                            drawPath(path, color = Color.White, style = Stroke(2f))
                        }
                    }
                    .padding(start = boundSpacing.dp, end = boundSpacing.dp)
                    .fillMaxWidth()
            ) {
                Text("Header", color = Color.White)
            }
//        NoPinContainer()
            Spacer(modifier = Modifier.height(verticalSpacing.dp))
            DoublePinRow()
            Spacer(modifier = Modifier.height(verticalSpacing.dp))
            repeat(10) {
                PinRow()
                Spacer(modifier = Modifier.height(verticalSpacing.dp))
            }
        }
    }
}


fun main() {
    singleWindowApplication {
        Surface(
            color = Color(30, 30, 30),
            modifier = Modifier.fillMaxSize()
        ) {
            VariablePreview()
        }
    }
}

