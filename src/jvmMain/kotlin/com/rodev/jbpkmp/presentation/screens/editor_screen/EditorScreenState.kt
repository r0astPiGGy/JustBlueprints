package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.*

class EditorScreenState(
    variables: List<GlobalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<GlobalVariableState>()
    var result: ScreenResult? by mutableStateOf(null)

    val isLoading by derivedStateOf {
        result is ScreenResult.Loading
    }

    init {
        this.variables.addAll(variables)
    }

    fun reset() {
        result = null
    }
}

sealed class ScreenResult {

    class SuccessUpload(val uploadCommand: String) : ScreenResult()

    class Loading(val state: LoadingState) : ScreenResult()

    class Error(val stage: LoadingState, val message: String?, val stackTrace: String?) : ScreenResult()

}

enum class LoadingState {
    SAVE,
    COMPILE,
    UPLOAD
}