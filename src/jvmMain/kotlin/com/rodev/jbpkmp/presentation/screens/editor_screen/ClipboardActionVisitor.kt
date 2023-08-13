package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.jbpkmp.domain.model.variable.GlobalVariable
import com.rodev.jbpkmp.domain.model.variable.LocalVariable
import com.rodev.nodeui.model.Node

interface ClipboardActionVisitor {

    fun pasteNode(nodeState: Node)

    fun pasteLocalVariable(variable: LocalVariable)

    fun pasteGlobalVariable(variable: GlobalVariable)

}