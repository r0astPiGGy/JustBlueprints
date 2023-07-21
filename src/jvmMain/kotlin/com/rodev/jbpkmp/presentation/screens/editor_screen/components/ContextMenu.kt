package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.rememberCursorPositionProvider
import java.awt.event.KeyEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContextMenu(
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var focusManager: FocusManager? by mutableStateOf(null)
    var inputModeManager: InputModeManager? by mutableStateOf(null)

    Popup(
        focusable = focusable,
        onDismissRequest = onDismissRequest,
        popupPositionProvider = rememberCursorPositionProvider(),
        onKeyEvent = {
            handlePopupOnKeyEvent(it, onDismissRequest, focusManager!!, inputModeManager!!)
        },
    ) {
        focusManager = LocalFocusManager.current
        inputModeManager = LocalInputModeManager.current

        ContextMenuContent (
            modifier = modifier,
            content = content
        )
    }
}

@Composable
internal fun ContextMenuContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        content = content
    )
}

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