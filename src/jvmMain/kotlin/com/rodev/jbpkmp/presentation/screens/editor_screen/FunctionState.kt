package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.runtime.*
import com.rodev.jbpkmp.util.generateUniqueId

class InvokableReference(
    val id: String,
    name: String,
) {
    var name: String by mutableStateOf(name)
}

abstract class InvokableState(
    val reference: InvokableReference,
    val graphState: GraphState,
) : Selectable, DragAndDropTarget {

    override var selected: Boolean by mutableStateOf(false)

    val id: String
        get() = reference.id

    val name: String by derivedStateOf {
        reference.name
    }

    private val clipboardEntry = ClipboardEntryImpl()

    fun onNameSet(name: String) {
        reference.name = name
    }

    override fun isClipboardEntryOwner(clipboardEntry: ClipboardEntry): Boolean {
        return this.clipboardEntry == clipboardEntry
    }

    override fun asClipboardEntry(): ClipboardEntry {
        return clipboardEntry
    }

    abstract fun onPaste(actionVisitor: ClipboardActionVisitor)

    private inner class ClipboardEntryImpl : ClipboardEntry {
        override fun onPaste(actionVisitor: ClipboardActionVisitor) {
            this@InvokableState.onPaste(actionVisitor)
        }
    }

}

class FunctionState(
    reference: InvokableReference,
    graphState: GraphState,
) : InvokableState(reference, graphState) {
    override fun onPaste(actionVisitor: ClipboardActionVisitor) {}

    override fun onDelete(actionVisitor: SelectionActionVisitor) {
        actionVisitor.deleteFunction(this)
    }

    @Composable
    override fun Details() {

    }

}

class ProcessState(
    reference: InvokableReference,
    graphState: GraphState,
) : InvokableState(reference, graphState) {
    override fun onPaste(actionVisitor: ClipboardActionVisitor) {}

    override fun onDelete(actionVisitor: SelectionActionVisitor) {
        actionVisitor.deleteProcess(this)
    }

    @Composable
    override fun Details() {

    }

}