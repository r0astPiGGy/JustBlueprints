package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel
import com.rodev.jbpkmp.util.generateUniqueId
import java.util.UUID

class GraphState(
    val viewModel: ViewPortViewModel,
    val id: String = generateUniqueId(),
    variables: List<LocalVariableState> = emptyList()
) {

    val variables = mutableStateListOf<LocalVariableState>()

    init {
        this.variables.addAll(variables)
    }

}