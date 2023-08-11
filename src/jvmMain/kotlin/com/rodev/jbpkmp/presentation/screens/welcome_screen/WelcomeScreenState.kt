package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WelcomeScreenState {

    var result: WelcomeScreenResult? by mutableStateOf(null)

    fun consume() {
        result = null
    }

}

sealed class WelcomeScreenResult {
    class Failure(val error: Error) : WelcomeScreenResult() {
        enum class Error {
            INVALID_PROJECT
        }
    }

    class OpenProject(val projectPath: String) : WelcomeScreenResult()

}