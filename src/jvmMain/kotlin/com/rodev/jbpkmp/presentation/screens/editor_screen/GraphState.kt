package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel

class GraphState(
    val viewModel: ViewPortViewModel,
    variables: List<LocalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<LocalVariableState>()

    init {
        this.variables.addAll(variables)
    }

}