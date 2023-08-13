package com.rodev.jbpkmp.presentation.screens.editor_screen

interface ClipboardEntry {

    fun onPaste(actionVisitor: ClipboardActionVisitor)

}