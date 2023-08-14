package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.repository.IconDataSource
import com.rodev.jbpkmp.presentation.localization.Vocabulary
import com.rodev.jbpkmp.presentation.localization.addPin
import com.rodev.nodeui.components.node.NodeState
import kotlin.math.roundToInt

@Composable
@Preview
fun ArrayNode(
    iconDataSource: IconDataSource,
    nodeState: NodeState,
    nodeEntity: NodeEntity,
    selected: Boolean,
    onTap: () -> Unit = {},
    onPinAdd: () -> Unit = {}
) {
    Surface(
        color = Color.Transparent,
        elevation = 10.dp,
        modifier = Modifier
            .wrapContentSize()
            .offset {
                IntOffset(
                    nodeState.x.roundToInt(),
                    nodeState.y.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    nodeState.x = (dragAmount.x + nodeState.x).coerceAtLeast(0f)
                    nodeState.y = (dragAmount.y + nodeState.y).coerceAtLeast(0f)

                    change.consume()
                }
            }
    ) {
        var bodyOffset by remember { mutableStateOf(Offset.Zero) }

        // CompositionLocal candidate
        val absoluteBodyPosition by remember {
            derivedStateOf {
                bodyOffset + Offset(nodeState.x, nodeState.y)
            }
        }

        Column(
            modifier = Modifier
                .padding(pinPadding.dp)
                .background(backgroundColor)
                .requiredWidth(IntrinsicSize.Max)
                .onGloballyPositioned {
                    bodyOffset = it.positionInParent()
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onTap()
                }
                .pointerHoverIcon(PointerIcon.Hand)
                .drawBehind {
                    if (selected) {
                        drawRect(color = Color.White, style = Stroke(2f))
                    }
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(nodeEntity.headerColor))
                    .drawWithCache {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            moveTo(0f, size.height)
                            lineTo(0f, 0f)
                        }
                        onDrawBehind {
                            if (selected) {
                                drawPath(path, color = Color.White, style = Stroke(2f))
                            }
                        }
                    }
                    .padding(start = boundSpacing.dp, end = boundSpacing.dp)
                    .fillMaxWidth()
            ) {
                val image = remember { iconDataSource.getIconById(nodeEntity.iconPath) }
                if (image != null) {
                    Image(
                        painter = BitmapPainter(image),
                        modifier = Modifier
                            .size(25.dp),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Text(nodeEntity.header, color = Color.White)
            }
            NodeSpacer()

            nodeState.outputPins.forEach {
                OutputPinRow(
                    nodeState = nodeState,
                    pinRowState = it,
                    absoluteBodyOffset = absoluteBodyPosition
                )
                NodeSpacer()
            }
            nodeState.inputPins.forEach {
                InputPinRow(
                    nodeState = nodeState,
                    pinRowState = it,
                    absoluteBodyOffset = absoluteBodyPosition
                )
                NodeSpacer()
            }

            Row(
                modifier = Modifier
                    .padding(start = boundSpacing.dp, end = boundSpacing.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onPinAdd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val path = Path().apply {
                        moveTo(center.x, 0f)
                        lineTo(center.x, size.height)
                        moveTo(0f, center.y)
                        lineTo(size.width, center.y)
                    }

                    drawPath(path, color = Color.White, style = Stroke(width = 1f))
                }

                Spacer(modifier = Modifier.width(5.dp))

                Text(Vocabulary.localization.addPin())
            }

            NodeSpacer()
        }
    }
}

@Composable
private fun NodeSpacer() {
    Spacer(modifier = Modifier.height(verticalSpacing.dp))
}
