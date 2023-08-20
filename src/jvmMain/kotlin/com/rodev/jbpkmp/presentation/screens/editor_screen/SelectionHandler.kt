package com.rodev.jbpkmp.presentation.screens.editor_screen

interface SelectionHandler {

    fun onSelect(selectable: Selectable)

    fun resetSelection()
}