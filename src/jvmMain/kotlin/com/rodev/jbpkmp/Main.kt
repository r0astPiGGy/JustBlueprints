package com.rodev.jbpkmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.theme.AppTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = false) {
            Screen()
        }
    }
}
