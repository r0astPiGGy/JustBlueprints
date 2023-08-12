package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.ui.geometry.Offset

sealed class EditorScreenEvent {
    object BuildProject : EditorScreenEvent()
    object SaveProject : EditorScreenEvent()
    object OpenSettingsScreen : EditorScreenEvent()
    object CloseSettingsScreen : EditorScreenEvent()
    object CloseProject : EditorScreenEvent()
    data class AddLocalVariable(val variable: LocalVariableState) : EditorScreenEvent()
    data class AddGlobalVariable(val variable: GlobalVariableState) : EditorScreenEvent()
    data class OnDragAndDrop(val variable: VariableState, val position: Offset) :
        EditorScreenEvent()
}
