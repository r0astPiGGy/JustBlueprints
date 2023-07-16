package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class TemporaryWire(
    val color: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
): Wire() {
    override fun DrawScope.drawFunction() {
        val path = getLinePath(startX, startY, endX, endY)

        drawPath(
            path = path,
            color = Color(color),
            style = Stroke(width = STROKE_WIDTH),
        )
    }
}
