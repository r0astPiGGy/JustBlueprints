package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import kotlin.math.cos
import kotlin.math.sin

val DefaultPinShape = CircleShape

val ExecPinShape = GenericShape { size, _ ->
    val center = size.center

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

private fun Offset.rotateBy(degrees: Float, from: Offset): Offset {
    val angle = (degrees) * (Math.PI / 180)

    return Offset(
        x = (cos(angle) * (x - from.x) - sin(angle) * (y - from.y) + from.x).toFloat(),
        y = (sin(angle) * (x - from.x) + cos(angle) * (y - from.y) + from.y).toFloat()
    )
}