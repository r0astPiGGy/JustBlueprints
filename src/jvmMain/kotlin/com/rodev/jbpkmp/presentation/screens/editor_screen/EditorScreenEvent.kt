package com.rodev.jbpkmp.presentation.screens.editor_screen

sealed class EditorScreenEvent {
    object BuildProject : EditorScreenEvent()
    object SaveProject : EditorScreenEvent()
}
