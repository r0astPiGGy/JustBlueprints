package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.theme.black
import com.rodev.nodeui.components.node.NodeState
import kotlin.math.max
import kotlin.math.roundToInt

const val nodeOutlinePadding = 6

@Composable
@Preview
fun NodePreview() {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.Transparent)
        ) {

        }
    }
}

@Composable
@Preview
fun SimpleNode(
    nodeState: NodeState,
    nodeEntity: NodeEntity,
    selected: Boolean,
    onTap: () -> Unit = {}
) {
    var nodeBodyRelativeCoordinates by remember { mutableStateOf(Offset.Zero) }

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
                nodeBodyRelativeCoordinates = it.positionInParent()
            }
    ) {
        NodeBody(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .defaultMinSize(minWidth = 100.dp)
                .wrapContentHeight()
                .clickable {
                    onTap()
                }
                .onGloballyPositioned {
                    nodeBodyRelativeCoordinates += it.positionInParent()
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .drawBehind {
                        drawRect(brush = Brush.radialGradient(
                            // TODO remove hardcoded values
                            colors = listOf(Color(nodeEntity.headerColor), black),
                            center = Offset(-size.height, -size.height * 2),
                            radius = (size.width)
                        ))
                    }
                    .padding(nodeOutlinePadding.dp)
                    .padding(end = 50.dp)
            ) {
                val image = remember { GlobalDataSource.getIconById(nodeEntity.iconPath) }
                if (image != null) {
                    Image(
                        painter = BitmapPainter(image),
                        modifier = Modifier
                            .size(30.dp),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Column {
                    Text(
                        text = nodeEntity.header,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colors.onBackground
                    )
                    if (nodeEntity.subHeader != null) {
                        Text(
                            text = nodeEntity.subHeader,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            PinContainer(
                alignment = Alignment.Start,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopStart)
                    .onGloballyPositioned {
                        nodeState.inputPinContainerPosition = it.positionInParent() + nodeBodyRelativeCoordinates
                    }
            ) {
                nodeState.inputPins.forEach {
                    it.pinRowDisplay.PinRowView(
                        nodeState = nodeState,
                        pinRowState = it
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            PinContainer(
                alignment = Alignment.End,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopEnd)
                    .onGloballyPositioned {
                        nodeState.outputPinContainerPosition = it.positionInParent() + nodeBodyRelativeCoordinates
                    }
            ) {
                nodeState.outputPins.forEach {
                    it.pinRowDisplay.PinRowView(
                        nodeState = nodeState,
                        pinRowState = it
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@Composable
private fun PinContainer(
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column (
        content = content,
        horizontalAlignment = alignment,
        modifier = Modifier
            .defaultMinSize(minWidth = 50.dp, minHeight = 50.dp)
            .padding(nodeOutlinePadding.dp)
            .then(modifier)
    )
}

@Composable
private fun NodeBody(modifier: Modifier, content: @Composable () -> Unit) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = nodeBodyMeasurePolicy()
    )
}

@Composable
private fun nodeBodyMeasurePolicy(): MeasurePolicy = remember {
    MeasurePolicy { measurables, constraints ->
        val inputContainer = measurables[1].measure(constraints)
        val outputContainer = measurables[2].measure(constraints)

        val containersWidth = inputContainer.width + outputContainer.width

        val header = measurables[0].measure(constraints.copy(minWidth = containersWidth))

        val spaceBetweenContainers = max(0, header.width - containersWidth)
        val height = header.height + max(inputContainer.height, outputContainer.height)
        val width = max(header.width, containersWidth)

        layout(width = width, height = height) {
            header.place(0, 0)
            inputContainer.place(0, header.height)
            outputContainer.place(inputContainer.width + spaceBetweenContainers, header.height)
        }
    }
}
