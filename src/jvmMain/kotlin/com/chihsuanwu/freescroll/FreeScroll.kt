package com.chihsuanwu.freescroll

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun Modifier.freeScroll(
    state: FreeScrollState,
    enabled: Boolean = true
): Modifier = composed {

    this.horizontalScroll(
        state = state.horizontalScrollState,
        enabled = false,
    ).verticalScroll(
        state = state.verticalScrollState,
        enabled = false,
    )
    .pointerInput(enabled, false, false) {
        if (!enabled) return@pointerInput

        coroutineScope {
            detectDragGestures(
                onDragStart = { },
                onDrag = { change, dragAmount ->
                    change.consume()
                    onDrag(
                        dragAmount = dragAmount,
                        state = state,
                        coroutineScope = this
                    )
                }
            )
        }
    }
}

private fun onDrag(
    dragAmount: Offset,
    state: FreeScrollState,
    coroutineScope: CoroutineScope,
) {
    coroutineScope.launch {
        state.horizontalScrollState.scrollBy(
            -dragAmount.x
        )
        state.verticalScrollState.scrollBy(
            -dragAmount.y
        )
    }
}
