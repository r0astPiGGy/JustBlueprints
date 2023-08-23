package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.ui.geometry.Offset
import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.Variable

sealed class EditorScreenEvent {
    object BuildProject : EditorScreenEvent()
    object SaveProject : EditorScreenEvent()
    object OpenSettingsScreen : EditorScreenEvent()
    object CloseSettingsScreen : EditorScreenEvent()
    object CloseProject : EditorScreenEvent()
    data class AddLocalVariable(val name: String) : EditorScreenEvent()
    data class AddGlobalVariable(val name: String, val type: Variable.Type) : EditorScreenEvent()
    data class AddProcess(val name: String) : EditorScreenEvent()
    data class AddFunction(val name: String) : EditorScreenEvent()
    data class OnProcessRename(val process: ProcessState, val name: String) : EditorScreenEvent()
    data class OnFunctionRename(val function: FunctionState, val name: String) : EditorScreenEvent()
    data class OpenFunction(val function: FunctionState) : EditorScreenEvent()
    data class OpenProcess(val process: ProcessState) : EditorScreenEvent()
    data class OnDragAndDrop(val target: DragAndDropTarget, val position: Offset) :
        EditorScreenEvent()
}
