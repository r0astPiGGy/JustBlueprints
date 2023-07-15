package com.rodev.jbpkmp.presentation.components.wire

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

interface Wire {

    fun drawFunction(): DrawScope.() -> Unit

    fun DrawScope.getLinePath(x1: Float, y1: Float, x2: Float, y2: Float): Path {
        val path = Path()

        path.moveTo(x1, y1)

        val controlPointX1 = (x1 + x2) * 0.5f // bottom
        val controlPointY1 = y1

        val controlPointX2 = (x1 + x2) * 0.5f // upper
        val controlPointY2 = y2

//        val debugCircle: DrawScope.(Float, Float) -> Unit = { x, y ->
//            drawCircle(Color.Red, radius = 5f, center = Offset(x, y))
//        }
//
//        debugCircle(controlPointX1, controlPointY1)
//        debugCircle(controlPointX2, controlPointY2)

        path.cubicTo(
            x1 = controlPointX1,
            y1 = controlPointY1,
            x2 = controlPointX2,
            y2 = controlPointY2,
            x3 = x2,
            y3 = y2,
        )

        return path
    }

    companion object {
        const val STROKE_WIDTH = 3f
    }

}