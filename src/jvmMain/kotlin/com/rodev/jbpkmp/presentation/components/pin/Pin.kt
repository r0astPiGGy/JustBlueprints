package com.rodev.jbpkmp.presentation.components.pin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private const val pinSize = 15

@Composable
fun InputPin(pinState: PinState) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Pin(pinState)
        Spacer(modifier = Modifier.size(6.dp))
        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
//            var inputText by remember { mutableStateOf("") }
//            TextField(value = inputText, onValueChange = { inputText = it }, singleLine = true)
        }
    }
}

@Composable
fun OutputPin(pinState: PinState) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.requiredSizeIn(maxWidth = 180.dp)
        ) {
            Text(
                text = pinState.entity.name
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
        Pin(pinState)
    }
}

@Composable
fun Pin(
    pinState: PinState
) {
    Canvas(modifier = Modifier.size(pinSize.dp)) {
        drawCircle(
            color = Color(pinState.entity.color),
            style = if (pinState.connected) Fill else Stroke(width = 2f)
        )
    }
}