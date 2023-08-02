package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.jbpkmp.domain.model.changeable_variable.Variable

data class EditorScreenState(
    val isLoading: Boolean,
    val globalVariables: MutableList<Variable> = mutableListOf()
)