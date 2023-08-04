package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.theme.black
import com.rodev.nodeui.components.node.NodeState
import kotlin.math.roundToInt

@Composable
fun VariableNode(
    nodeState: NodeState,
    header: String,
    subHeader: String,
    selected: Boolean,
    onTap: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = if (selected) BorderStroke(3.dp, Color.Yellow) else null,
        modifier = Modifier
            .offset { IntOffset(
                nodeState.x.roundToInt().coerceAtLeast(0),
                nodeState.y.roundToInt().coerceAtLeast(0)
            ) }
            .wrapContentSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    nodeState.x += dragAmount.x
                    nodeState.y += dragAmount.y
                }
            }
            .onGloballyPositioned {
                nodeState.outputPinContainerPosition = it.positionInParent()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .drawBehind {
                    drawRect(brush = Brush.radialGradient(
                        // TODO remove hardcoded values
                        colors = listOf(Color.Blue, black),
                        center = Offset(0f, -size.height * 2),
                        radius = (size.width)
                    ))
                }
                .defaultMinSize(minWidth = 100.dp)
                .wrapContentHeight()
                .clickable {
                    onTap()
                }
                .onGloballyPositioned {
                    nodeState.outputPinContainerPosition += it.positionInParent()
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .widthIn(min = 100.dp, max = 300.dp)
                    .padding(nodeOutlinePadding.dp)
                    .padding(end = 50.dp)
            ) {
                Column {
                    Text(
                        text = header,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = subHeader,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            val outputPin = nodeState.outputPins.first()

            outputPin.pinRowDisplay.PinRowView(
                nodeState = nodeState,
                pinRowState = outputPin
            )

            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}