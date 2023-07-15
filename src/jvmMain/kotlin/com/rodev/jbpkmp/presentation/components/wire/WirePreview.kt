package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

data class WirePreview(
    val color: Int,
    val colorEnd: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
): Wire {
    override fun drawFunction(): DrawScope.() -> Unit = {
        val path = getLinePath(startX, startY, endX, endY)

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                listOf(Color(color), Color(colorEnd)),
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            alpha = 0.4f,
            style = Stroke(width = Wire.STROKE_WIDTH),
        )
    }
}
