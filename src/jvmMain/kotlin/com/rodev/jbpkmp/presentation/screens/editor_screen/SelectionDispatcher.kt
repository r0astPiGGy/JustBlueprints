package com.rodev.jbpkmp.presentation.screens.editor_screen

class SelectionDispatcher : SelectionHandler {

    private val selectionHandlers = mutableListOf<SelectionHandler>()

    fun registerSelectionHandler(selectionHandler: SelectionHandler) {
        selectionHandlers += selectionHandler
    }

    fun unregisterSelectionHandler(selectionHandler: SelectionHandler) {
        selectionHandlers.remove(selectionHandler)
    }

    override fun onSelect(selectable: Selectable) {
        selectionHandlers.forEach { it.onSelect(selectable) }
    }

    override fun resetSelection() {
        selectionHandlers.forEach { it.resetSelection() }
    }

}