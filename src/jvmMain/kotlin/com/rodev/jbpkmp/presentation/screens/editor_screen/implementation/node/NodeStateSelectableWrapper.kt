package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.screens.editor_screen.ClipboardActionVisitor
import com.rodev.jbpkmp.presentation.screens.editor_screen.ClipboardEntry
import com.rodev.jbpkmp.presentation.screens.editor_screen.Selectable
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionActionVisitor
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node

class NodeStateSelectableWrapper(
    private val selectSetter: (Boolean) -> Unit,
    private val selectGetter: () -> Boolean,
    private val nodeSupplier: () -> Node,
    private val nodeState: NodeState,
    private val detailsComposable: @Composable () -> Unit,
    private val copyEnabled: Boolean = true,
    private val deletionEnabled: Boolean = true,
) : Selectable {

    private val clipboardEntry = ClipboardEntryImpl()

    override var selected: Boolean
        get() = selectGetter()
        set(value) {
            selectSetter(value)
        }

    override fun onDelete(actionVisitor: SelectionActionVisitor) {
        if (deletionEnabled) {
            actionVisitor.deleteNode(nodeState)
        }
    }

    override fun isClipboardEntryOwner(clipboardEntry: ClipboardEntry): Boolean {
        return this.clipboardEntry == clipboardEntry
    }

    override fun asClipboardEntry(): ClipboardEntry {
        return clipboardEntry
    }

    @Composable
    override fun Details() {
        detailsComposable()
    }

    private inner class ClipboardEntryImpl : ClipboardEntry {
        override fun onPaste(actionVisitor: ClipboardActionVisitor) {
            if (copyEnabled) {
                actionVisitor.pasteNode(nodeSupplier())
            }
        }

    }

}