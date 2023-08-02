package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

typealias DraggableComposable = (@Composable () -> Unit)

val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<DraggableComposable?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}

@Composable
fun DraggableContext(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(modifier = modifier) {
            content()
            if (state.isDragging) {
                var targetSize by remember { mutableStateOf(IntSize.Zero) }
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val offset = (state.dragPosition + state.dragOffset)
                            scaleX = 1.3f
                            scaleY = 1.3f
                            alpha = if (targetSize == IntSize.Zero) 0f else .9f
                            translationX = offset.x.minus(targetSize.width / 2)
                            translationY = offset.y.minus(targetSize.height / 2)
                        }
                        .onGloballyPositioned {
                            targetSize = it.size
                        }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: Any?,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(
        modifier = modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    currentState.dataToDrop = dataToDrop
                    currentState.isDragging = true
                    currentState.dragPosition = currentPosition + it
                    currentState.draggableComposable = content
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                }, onDragEnd = {
                    currentState.isDragging = false
                    currentState.dragOffset = Offset.Zero
                }, onDragCancel = {
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                })
            }
    ) {
        content()
    }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> DropTarget(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.(isInBound: Boolean, data: T?, position: Offset) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember { mutableStateOf(false) }
    val dataAvailable by derivedStateOf {
        isCurrentDropTarget && !dragInfo.isDragging
    }
    var localPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInWindow().let { rect ->
                    isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
                }
                localPosition = it.windowToLocal(dragPosition + dragOffset)
            }
    ) {
        val data = if (dataAvailable) {
            dragInfo.dataToDrop as T?
        } else {
            null
        }

        content(isCurrentDropTarget, data, localPosition)
    }
}