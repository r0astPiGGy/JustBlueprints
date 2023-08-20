package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.rodev.generator.action.entity.NodeModel
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.source.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.InvokableReference
import com.rodev.jbpkmp.presentation.screens.editor_screen.InvokableReferenceProvider
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.getInvokableId
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.model.Node

class InvokableReferenceNodeStateFactory(
    private val invokableReferenceProvider: InvokableReferenceProvider,
    nodeDataSource: NodeDataSource,
    nodeTypeDataSource: NodeTypeDataSource,
    actionDataSource: ActionDataSource,
    selectionHandler: SelectionHandler,
    actionDetailsDataSource: ActionDetailsDataSource,
    iconDataSource: IconDataSource,
    selectorDataSource: SelectorDataSource,
    pinTypeDataSource: PinTypeDataSource
) : DefaultNodeStateFactory(
    nodeDataSource,
    nodeTypeDataSource,
    actionDataSource,
    selectionHandler,
    actionDetailsDataSource,
    iconDataSource,
    selectorDataSource,
    pinTypeDataSource
) {

    override fun getNodeRepresentation(node: Node, nodeModel: NodeModel): NodeDisplay {
        val typeId = nodeModel.id
        val nodeType = nodeTypeDataSource[nodeModel.type]!!
        val action = actionDataSource.getActionById(typeId)!!
        val invokableId = node.getInvokableId()
        val reference = invokableReferenceProvider.getInvokableReferenceById(invokableId)!!

        return InvokableReferenceDisplay(
            invokableId = invokableId,
            nodeEntity = InvokableNodeEntity(
                id = typeId,
                reference = reference,
                originalName = action.name,
                headerColor = nodeType.color,
                iconPath = action.iconPath
            ),
            selectionHandler = selectionHandler,
            actionDetails = actionDetailsDataSource[typeId],
            iconDataSource = iconDataSource
        )
    }

    private class InvokableNodeEntity(
        val reference: InvokableReference,
        private val originalName: String,
        override val id: String,
        override val subHeader: String? = null,
        override val headerColor: Int,
        override val iconPath: String
    ) : NodeEntity {

        override val header: String by derivedStateOf {
            originalName.format(reference.name)
        }

    }

}