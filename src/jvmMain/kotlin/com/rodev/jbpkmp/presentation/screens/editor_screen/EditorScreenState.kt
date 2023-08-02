package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class EditorScreenState(
    isLoading: Boolean,
    variables: List<GlobalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<GlobalVariableState>()
    var isLoading by mutableStateOf(isLoading)

    init {
        this.variables.addAll(variables)
    }

}