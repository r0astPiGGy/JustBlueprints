package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.generator.action.entity.NodeModel
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.source.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.nodeui.components.node.NodeDisplay
import com.rodev.nodeui.model.Node

class CollapsibleActionNodeStateFactory(
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
        val actionDetails = actionDetailsDataSource.getActionDetailsById(typeId)
        val hidden = node.tag.getBoolean(FactoryActionNodeDisplay.HIDDEN_TAG) ?: false

        return FactoryActionNodeDisplay(
            nodeEntity = NodeEntity(
                id = typeId,
                header = action.name,
                headerColor = nodeType.color,
                iconPath = action.iconPath
            ),
            selectionHandler = selectionHandler,
            iconDataSource = iconDataSource,
            actionDetails = actionDetails,
            hiddenInitially = hidden
        )
    }

}