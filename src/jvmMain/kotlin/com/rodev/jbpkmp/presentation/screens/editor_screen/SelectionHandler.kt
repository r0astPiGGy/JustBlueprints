package com.rodev.jbpkmp.presentation.screens.editor_screen

interface SelectionHandler {

    fun onSelect(selectable: Selectable)

    fun resetSelection()

    companion object Default : SelectionHandler {
        override fun onSelect(selectable: Selectable) {}
        override fun resetSelection() {}
    }
}