package com.rodev.nodeui.components.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import com.chihsuanwu.freescroll.freeScroll
import com.chihsuanwu.freescroll.rememberFreeScrollState


@Composable
fun GraphViewPort(
    modifier: Modifier = Modifier,
    viewModel: GraphViewModel,
    content: @Composable GraphViewModel.() -> Unit
) {
    Box(
        modifier = modifier
            .clipToBounds()
            .freeScroll(viewModel.scrollState)
    ) {
        content(viewModel)
    }
}

@Composable
fun GraphLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }

        layout(Int.MAX_VALUE, Int.MAX_VALUE) {
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = 0)
            }
        }
    }
}
