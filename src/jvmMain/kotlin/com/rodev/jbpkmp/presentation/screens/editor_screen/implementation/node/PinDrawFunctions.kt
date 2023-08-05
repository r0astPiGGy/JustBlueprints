package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.pinOutline
import com.rodev.nodeui.components.pin.PinDrawFunction
import com.rodev.nodeui.components.pin.PinState
import kotlin.math.cos
import kotlin.math.sin

object DefaultDrawFunction : PinDrawFunction {
    override fun DrawScope.onDraw(pinState: PinState) {
        drawCircle(color = pinOutline, style = Stroke(width = 2f))
        drawCircle(color = Color(pinState.pinDisplay.color))
//        drawCircle(
//            color = Color(pinState.pinDisplay.color),
//            style = if (pinState.connected) Fill else Stroke(width = 2f)
//        )
    }
}

object ExecDrawFunction : PinDrawFunction {

    override fun DrawScope.onDraw(pinState: PinState) {
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

//        val path = Path()
//
//        val center = size.center
//
//        path.moveTo(0f, 0f)
//        path.lineTo(center.x, 0f)
//        path.lineTo(size.width, center.y)
//        path.lineTo(center.x, size.height)
//        path.lineTo(0f, size.height)
//        path.lineTo(0f, 0f)
//
//        val style: DrawStyle = if (pinState.connected) Fill else Stroke(width = 1.3f)
//
//        drawPath(
//            path = path,
//            // white
//            color = Color(pinState.pinDisplay.color),
//            style = style
//        )
    }

}

private fun Offset.rotateBy(degrees: Float, from: Offset): Offset {
    val angle = (degrees) * (Math.PI / 180)

    return Offset(
        x = (cos(angle) * (x - from.x) - sin(angle) * (y - from.y) + from.x).toFloat(),
        y = (sin(angle) * (x - from.x) + cos(angle) * (y - from.y) + from.y).toFloat()
    )
}