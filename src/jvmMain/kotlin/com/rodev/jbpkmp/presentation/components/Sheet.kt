package com.rodev.jbpkmp.presentation.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.handlePopupOnKeyEvent

@Composable
fun Sheet(
    presented: Boolean,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = presented

    if (transitionState.targetState || transitionState.currentState) {
        var focusManager: FocusManager? by mutableStateOf(null)
        var inputModeManager: InputModeManager? by mutableStateOf(null)

        Popup(
            focusable = true,
            onDismissRequest = onDismissRequest,
            onKeyEvent = {
                handlePopupOnKeyEvent(
                    it,
                    onDismissRequest,
                    focusManager!!,
                    inputModeManager!!
                )
            },
        ) {
            focusManager = LocalFocusManager.current
            inputModeManager = LocalInputModeManager.current

            val transition = updateTransition(transitionState)
            val color by transition.animateColor(
                transitionSpec = { tween() }
            ) {
                if (it) {
                    Color.Black.copy(alpha = 0.32f)
                } else {
                    Color.Transparent
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(onDismissRequest) {
                        detectTapGestures(onPress = { onDismissRequest() })
                    }
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                SheetContent(transitionState, content)
            }
        }
    }
}

@Composable
private fun SheetContent(
    transitionState: MutableTransitionState<Boolean>,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(transitionState)
    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            } else {
                tween(
                    durationMillis = 100,
                    easing = LinearOutSlowInEasing
                )
            }
        }
    ) {
        if (it) {
            1.0f
        } else {
            0.8f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween()
            } else {
                spring()
            }
        }
    ) {
        if (it) {
            1.0f
        } else {
            0.0f
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(alpha)
            .pointerInput(Unit) {
                detectTapGestures {  }
            }
    ) {
        content()
    }

}