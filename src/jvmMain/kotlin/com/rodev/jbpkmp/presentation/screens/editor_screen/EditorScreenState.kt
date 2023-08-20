package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.*
import com.rodev.jbpkmp.domain.model.CodeLoadCommand

class EditorScreenState(
    forceCodeLoad: Boolean = false
) {

    var result: EditorScreenResult? by mutableStateOf(null)
    var showSettingsScreen by mutableStateOf(false)

    val isLoading by derivedStateOf {
        result is EditorScreenResult.Loading
    }

    var forceCodeLoad by mutableStateOf(forceCodeLoad)
    var navigationResult: NavigationResult by mutableStateOf(NavigationResult.Empty)

    fun reset() {
        navigationResult = NavigationResult.Empty
        result = null
    }
}

sealed class NavigationResult {
    object Empty : NavigationResult()
    object GoBack : NavigationResult()
}

sealed class EditorScreenResult {

    class SuccessUpload(val uploadCommand: CodeLoadCommand) : EditorScreenResult()

    class Loading(val state: LoadingState) : EditorScreenResult()

    class Error(val stage: LoadingState, val message: String?, val stackTrace: String?) : EditorScreenResult()

    class RuntimeError(val message: String?, val stackTrace: String?) : EditorScreenResult()

}

enum class LoadingState {
    SAVE,
    LOAD,
    COMPILE,
    UPLOAD
}