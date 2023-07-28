package com.rodev.jbpkmp.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

fun randomColor(): Int {
    return Color(
        red = Random.nextInt(),
        green = Random.nextInt(),
        blue = Random.nextInt()
    ).toArgb()
}