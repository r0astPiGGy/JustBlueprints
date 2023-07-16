package com.rodev.jbpkmp.presentation.components.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.Layout
import com.chihsuanwu.freescroll.freeScroll
import com.chihsuanwu.freescroll.rememberFreeScrollState

@Composable
fun GraphViewPort(
    modifier: Modifier = Modifier,
    graphModifier: Modifier = Modifier,
    initialX: Int = 0,
    initialY: Int = 0,
    viewModel: GraphViewModel = rememberGraphViewModel(),
    content: @Composable GraphViewModel.() -> Unit
) {
    val scrollState = rememberFreeScrollState(
        initialX = initialX,
        initialY = initialY
    )
    Box(
        modifier = modifier
            .clipToBounds()
            .freeScroll(scrollState)
    ) {
        GraphLayout(
            modifier = graphModifier
                .drawBehind {
                    viewModel.temporaryLine.value?.draw(this)
                    viewModel.lines.forEach { it.draw(this) }
                },
        ) {
            content(viewModel)
        }
    }
}

@Composable
fun rememberGraphViewModel() = remember {
    GraphViewModel()
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
