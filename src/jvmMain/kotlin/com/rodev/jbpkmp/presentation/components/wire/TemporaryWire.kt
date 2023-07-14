package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

data class TemporaryWire(
    val color: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
): Wire {
    override fun drawFunction(): DrawScope.() -> Unit = {
        drawLine(
            Color(color),
            strokeWidth = 3f,
            start = Offset(startX, startY),
            end = Offset(endX, endY)
        )
    }
}
