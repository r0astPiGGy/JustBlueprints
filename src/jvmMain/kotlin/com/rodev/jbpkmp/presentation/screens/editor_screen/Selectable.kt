package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.Composable

interface Selectable {

    var selected: Boolean

    fun onDelete(actionVisitor: SelectionActionVisitor)

    fun isClipboardEntryOwner(clipboardEntry: ClipboardEntry): Boolean

    fun asClipboardEntry(): ClipboardEntry

    @Composable
    fun Details()

}