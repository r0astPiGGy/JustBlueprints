package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.jbpkmp.domain.model.changeable_variable.Variable
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.ViewPortViewModel

data class GraphModel(
    val name: String,
    val viewModel: ViewPortViewModel,
    val variables: MutableList<Variable> = mutableListOf()
)