package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.VariableNodeState
import com.rodev.nodeui.components.node.NodeState
import kotlin.math.roundToInt

@Composable
fun VariableNode(
    nodeState: NodeState,
    state: VariableNodeState,
    onTap: () -> Unit = {}
) {
    val pinRowState = nodeState.outputPins.first()

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
        var rowOffset by remember { mutableStateOf(Offset.Zero) }
        val absoluteBodyPosition by remember {
            derivedStateOf {
                bodyOffset + Offset(nodeState.x, nodeState.y) + rowOffset
            }
        }

        Column(
            modifier = Modifier
                .padding(pinPadding.dp)
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
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .drawBehind {
                        val cornerRadius = CornerRadius(5f)
                        drawRoundRect(color = backgroundColor, cornerRadius = cornerRadius)
                        if (state.selected) {
                            drawRoundRect(
                                color = Color.White,
                                cornerRadius = cornerRadius,
                                style = Stroke(2f)
                            )
                        }
                    }
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            rowOffset = it.positionInParent()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = boundSpacing.dp,
                                    end = 40.dp
                                )
                        ) {
                            Text(
                                text = state.name,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                                maxLines = 1,
                            )
                            Text(
                                text = state.subHeader.typeName,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontSize = 13.sp,
                                color = Color.White,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        OutputPinRow(
                            nodeState = nodeState,
                            absoluteBodyOffset = absoluteBodyPosition,
                            pinRowState = pinRowState
                        )
                    }
                }
            }
        }
    }
}