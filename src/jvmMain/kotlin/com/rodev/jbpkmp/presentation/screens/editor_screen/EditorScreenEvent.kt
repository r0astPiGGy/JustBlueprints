package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.jbpkmp.domain.model.changeable_variable.Variable

sealed class EditorScreenEvent {
    object BuildProject : EditorScreenEvent()
    object SaveProject : EditorScreenEvent()
    data class AddLocalVariable(val variable: Variable) : EditorScreenEvent()
    data class AddGlobalVariable(val variable: Variable) : EditorScreenEvent()
}
