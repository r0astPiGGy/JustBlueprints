package com.rodev.jbpkmp

import androidx.compose.material.Scaffold
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.presentation.screens.WelcomeScreen
import com.rodev.jbpkmp.theme.AppTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        AppTheme(useDarkTheme = false) {
            Scaffold {
                WelcomeScreen()
            }
        }
    }
}
