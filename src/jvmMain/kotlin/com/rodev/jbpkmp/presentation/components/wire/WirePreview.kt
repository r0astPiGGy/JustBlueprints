package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

data class WirePreview(
    val color: Int,
    val colorEnd: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
): Wire {
    override fun drawFunction(): DrawScope.() -> Unit = {
        drawLine(
            // todo fix gradient
            Brush.linearGradient(0.0f to Color(color), 1f to Color(colorEnd)),
            alpha = 0.4f,
            strokeWidth = 3f,
            start = Offset(startX, startY),
            end = Offset(endX, endY)
        )
    }
}
