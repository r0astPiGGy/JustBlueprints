package com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberCursorPositionProvider
import java.awt.event.KeyEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {
        val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }

        var focusManager: FocusManager? by mutableStateOf(null)
        var inputModeManager: InputModeManager? by mutableStateOf(null)

        Popup(
            focusable = focusable,
            onDismissRequest = onDismissRequest,
            popupPositionProvider = rememberCursorPositionProvider(),
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

            ContextMenuContent(
                expandedStates = expandedStates,
                transformOriginState = transformOriginState,
                modifier = modifier,
                content = content
            )
        }
    }
}

@Composable
internal fun ContextMenuContent(
    expandedStates: MutableTransitionState<Boolean>,
    transformOriginState: MutableState<TransformOrigin>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    // Menu open/close animation.
    val transition = updateTransition(expandedStates, "ContextMenu")

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(
                    durationMillis = InTransitionDuration,
                    easing = LinearOutSlowInEasing
                )
            } else {
                // Expanded to dismissed.
                tween(
                    durationMillis = 1,
                    delayMillis = OutTransitionDuration - 1
                )
            }
        }
    ) {
        if (it) {
            // Menu is expanded.
            1f
        } else {
            // Menu is dismissed.
            0.8f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = 30)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = OutTransitionDuration)
            }
        }
    ) {
        if (it) {
            // Menu is expanded.
            1f
        } else {
            // Menu is dismissed.
            0f
        }
    }
    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = transformOriginState.value
            }
            .padding(vertical = DropdownMenuVerticalPadding)
            .width(IntrinsicSize.Max)
            .verticalScroll(rememberScrollState()),
        content = content
    )
}


// Size defaults.
internal val DropdownMenuVerticalPadding = 8.dp

// Menu open/close animation.
internal const val InTransitionDuration = 120
internal const val OutTransitionDuration = 75

@ExperimentalComposeUiApi
private fun handlePopupOnKeyEvent(
    keyEvent: androidx.compose.ui.input.key.KeyEvent,
    onDismissRequest: () -> Unit,
    focusManager: FocusManager,
    inputModeManager: InputModeManager
): Boolean {
    return if (keyEvent.type == KeyEventType.KeyDown && keyEvent.awtEventOrNull?.keyCode == KeyEvent.VK_ESCAPE) {
        onDismissRequest()
        true
    } else if (keyEvent.type == KeyEventType.KeyDown) {
        when (keyEvent.nativeKeyEvent) {
            KeyEvent.VK_DOWN -> {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                focusManager.moveFocus(FocusDirection.Next)
                true
            }

            KeyEvent.VK_UP -> {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                focusManager.moveFocus(FocusDirection.Previous)
                true
            }

            else -> false
        }
    } else {
        false
    }
}