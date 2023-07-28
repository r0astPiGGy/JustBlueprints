package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node

import com.rodev.jbpkmp.data.iconPath
import com.rodev.jbpkmp.domain.model.NodeEntity
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import com.rodev.jbpkmp.domain.repository.NodeDataSource
import com.rodev.jbpkmp.util.randomColor
import com.rodev.nodeui.components.node.NodeRepresentation
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.row.PinRowStateFactory

class DefaultNodeStateFactory(
    pinRowStateFactory: PinRowStateFactory,
    private val nodeDataSource: NodeDataSource,
    private val actionDataSource: ActionDataSource
) : NodeStateFactory(pinRowStateFactory) {

    override fun getNodeRepresentation(typeId: String): NodeRepresentation {
        val node = nodeDataSource.getNodeModelById(typeId)
        val action = actionDataSource.getActionById(typeId)

        return DefaultNodeRepresentation(
            NodeEntity(
                id = typeId,
                header = action.name,
                headerColor = randomColor(),
                iconPath = action.iconPath
            )
        )
    }

}

