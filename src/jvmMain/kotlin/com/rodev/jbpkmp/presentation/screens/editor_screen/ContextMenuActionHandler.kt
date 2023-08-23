package com.rodev.jbpkmp.presentation.screens.editor_screen

interface ContextMenuActionHandler {

    fun onEvent(event: ContextMenuEvent)

}

sealed class ContextMenuEvent {

    class RenameProcess(val process: ProcessState) : ContextMenuEvent()
    class DeleteProcess(val process: ProcessState) : ContextMenuEvent()

    class RenameFunction(val function: FunctionState) : ContextMenuEvent()
    class DeleteFunction(val function: FunctionState) : ContextMenuEvent()

    class CopyVariable(val variable: VariableState) : ContextMenuEvent()
    class PasteVariable(val variable: VariableState) : ContextMenuEvent()
    class DeleteVariable(val variable: VariableState) : ContextMenuEvent()

}