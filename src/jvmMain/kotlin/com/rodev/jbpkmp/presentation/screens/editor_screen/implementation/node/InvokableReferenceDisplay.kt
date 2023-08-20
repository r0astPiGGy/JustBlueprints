package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.ActionDetails
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.source.IconDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.inheritBuilder
import com.rodev.jbpkmp.presentation.screens.editor_screen.setInvokableId
import com.rodev.nodeui.components.node.NodeState
import com.rodev.nodeui.model.Node

class InvokableReferenceDisplay(
    val invokableId: String,
    nodeEntity: NodeEntity,
    iconDataSource: IconDataSource,
    selectionHandler: SelectionHandler,
    actionDetails: ActionDetails?,
    copyEnabled: Boolean = true,
    deletionEnabled: Boolean = true,
) : DefaultNodeDisplay(
    nodeEntity,
    selectionHandler,
    iconDataSource,
    actionDetails,
    copyEnabled,
    deletionEnabled
) {

    override fun toNode(nodeState: NodeState): Node {
        val node = super.toNode(nodeState)

        return node.copy(
            tag = node.tag.inheritBuilder {
                setInvokableId(invokableId)
            }
        )
    }

}
