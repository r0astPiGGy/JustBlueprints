package com.rodev.jbpkmp.presentation.screens.editor_screen

interface VariableStateProvider {

    fun getVariableStateById(id: String): VariableState?

}