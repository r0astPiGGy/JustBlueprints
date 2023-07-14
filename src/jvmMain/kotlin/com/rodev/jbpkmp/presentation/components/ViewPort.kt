package com.rodev.jbpkmp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import com.chihsuanwu.freescroll.freeScroll
import com.chihsuanwu.freescroll.rememberFreeScrollState
import com.rodev.jbpkmp.presentation.viewmodel.ViewPortViewModel

@Composable
fun ViewPort(
    modifier: Modifier = Modifier,
    viewPortModifier: Modifier = Modifier,
    initialX: Int = 0,
    initialY: Int = 0,
    viewModel: ViewPortViewModel = rememberViewPortViewModel(),
    content: @Composable ViewPortViewModel.() -> Unit
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
        ViewPortLayout(modifier = viewPortModifier) {
            content(viewModel)
        }
    }
}

@Composable
fun rememberViewPortViewModel() = remember {
    ViewPortViewModel()
}

@Composable
fun ViewPortLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints)
        }

        layout(Short.MAX_VALUE - 1, Short.MAX_VALUE - 1) {
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = 0)
            }
        }
    }
}
