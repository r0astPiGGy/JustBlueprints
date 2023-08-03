package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.jbpkmp.presentation.screens.editor_screen.Selectable
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionActionVisitor
import com.rodev.nodeui.components.node.NodeState

class NodeStateSelectableWrapper(
    private val selectSetter: (Boolean) -> Unit,
    private val selectGetter: () -> Boolean,
    private val nodeState: NodeState
) : Selectable {

    override var selected: Boolean
        get() = selectGetter()
        set(value) {
            selectSetter(value)
        }

    override fun onDelete(selectionActionVisitor: SelectionActionVisitor) {
        selectionActionVisitor.deleteNode(nodeState)
    }

}