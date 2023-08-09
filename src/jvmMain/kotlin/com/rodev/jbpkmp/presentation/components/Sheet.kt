package com.rodev.jbpkmp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.window.Popup
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.handlePopupOnKeyEvent
import kotlinx.serialization.json.JsonNull.content

@Composable
fun Sheet(
    presented: Boolean,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit
) {
    if (presented) {
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f)),
                contentAlignment = Alignment.Center
            ) { content() }
        }
    }

}