package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.*

class EditorScreenState(
    variables: List<GlobalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<GlobalVariableState>()
    var result: EditorScreenResult? by mutableStateOf(null)
    var showSettingsScreen by mutableStateOf(false)

    val isLoading by derivedStateOf {
        result is EditorScreenResult.Loading
    }

    var navigationResult: NavigationResult by mutableStateOf(NavigationResult.Empty)

    init {
        this.variables.addAll(variables)
    }

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

    class SuccessUpload(val uploadCommand: String) : EditorScreenResult()

    class Loading(val state: LoadingState) : EditorScreenResult()

    class Error(val stage: LoadingState, val message: String?, val stackTrace: String?) : EditorScreenResult()

}

enum class LoadingState {
    SAVE,
    COMPILE,
    UPLOAD
}