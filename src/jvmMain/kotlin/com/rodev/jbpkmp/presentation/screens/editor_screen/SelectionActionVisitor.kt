package com.rodev.jbpkmp.presentation.screens.editor_screen

import com.rodev.nodeui.components.node.NodeState

interface SelectionActionVisitor {

    fun deleteNode(nodeState: NodeState)

    fun deleteLocalVariable(variable: LocalVariableState)

    fun deleteGlobalVariable(variable: GlobalVariableState)

}