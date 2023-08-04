package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.rodev.nodeui.components.pin.PinDrawFunction
import com.rodev.nodeui.components.pin.PinState

object DefaultDrawFunction : PinDrawFunction {
    override fun DrawScope.onDraw(pinState: PinState) {
        drawCircle(
            color = Color(pinState.pinDisplay.color),
            style = if (pinState.connected) Fill else Stroke(width = 2f)
        )
    }
}

object ExecDrawFunction : PinDrawFunction {

    override fun DrawScope.onDraw(pinState: PinState) {
        val path = Path()

        val center = size.center

        path.moveTo(0f, 0f)
        path.lineTo(center.x, 0f)
        path.lineTo(size.width, center.y)
        path.lineTo(center.x, size.height)
        path.lineTo(0f, size.height)
        path.lineTo(0f, 0f)

        val style: DrawStyle = if (pinState.connected) Fill else Stroke(width = 1.3f)

        drawPath(
            path = path,
            // white
            color = Color(pinState.pinDisplay.color),
            style = style
        )
    }

}