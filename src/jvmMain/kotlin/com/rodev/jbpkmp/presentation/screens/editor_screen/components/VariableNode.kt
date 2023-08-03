package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.theme.black
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.components.pin.PinDragListener
import com.rodev.nodeui.components.pin.row.SnapshotRequester
import com.rodev.nodeui.util.MutableCoordinate
import kotlin.math.roundToInt

@Composable
fun VariableNode(
    nodeState: NodeState,
    header: String,
    subHeader: String,
    pinDragListener: PinDragListener,
    snapshotRequester: SnapshotRequester,
    selected: Boolean,
    onTap: () -> Unit = {}
) {
    val nodeBodyRelativeCoordinates = remember {
        MutableCoordinate()
    }
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
                it.positionInParent().apply {
                    nodeBodyRelativeCoordinates.x = x
                    nodeBodyRelativeCoordinates.y = y
                }
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
                    it.positionInParent().apply {
                        nodeBodyRelativeCoordinates.x += x
                        nodeBodyRelativeCoordinates.y += y
                    }
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

            outputPin.pinRowRepresentation.onDraw(
                nodeState = nodeState,
                pinRowState = outputPin,
                pinDragListener = pinDragListener,
                snapshotRequester = snapshotRequester,
                parentCoordinate = nodeBodyRelativeCoordinates
            )

            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}