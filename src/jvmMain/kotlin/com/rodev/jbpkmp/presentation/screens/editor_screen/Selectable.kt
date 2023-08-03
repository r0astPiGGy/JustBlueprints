package com.rodev.jbpkmp.presentation.screens.editor_screen

interface Selectable {

    var selected: Boolean

    fun onDelete(selectionActionVisitor: SelectionActionVisitor)

}