package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.Composable

interface Selectable {

    var selected: Boolean

    fun onDelete(selectionActionVisitor: SelectionActionVisitor)

    @Composable
    fun Details()

}