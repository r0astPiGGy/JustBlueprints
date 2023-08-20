package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

interface ViewPortViewModelFactory {

    fun createEventGraphViewModel(): ViewPortViewModel

    fun createFunctionGraphViewModel(): ViewPortViewModel

    fun createProcessGraphViewModel(): ViewPortViewModel

}