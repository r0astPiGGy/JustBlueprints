package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.*

class EditorScreenState(
    variables: List<GlobalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<GlobalVariableState>()
    var result: EditorScreenResult? by mutableStateOf(null)

    val isLoading by derivedStateOf {
        result is EditorScreenResult.Loading
    }

    init {
        this.variables.addAll(variables)
    }

    fun reset() {
        result = null
    }
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