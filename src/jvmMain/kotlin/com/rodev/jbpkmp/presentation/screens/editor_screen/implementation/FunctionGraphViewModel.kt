package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.ActionType
import com.rodev.jbpkmp.domain.source.ActionDataSource
import com.rodev.jbpkmp.domain.source.ActionDetailsDataSource
import com.rodev.jbpkmp.domain.source.IconDataSource
import com.rodev.jbpkmp.domain.source.NodeDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.getType
import com.rodev.nodeui.components.graph.GraphEvent
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.node.NodeStateFactory

class FunctionGraphViewModel(
    private val actionDataSource: ActionDataSource,
    detailsDataSource: ActionDetailsDataSource,
    pinTypeComparator: PinTypeComparator,
    nodeStateFactory: NodeStateFactory,
    nodeDataSource: NodeDataSource,
    iconDataSource: IconDataSource
) : ViewPortViewModel(
    pinTypeComparator = pinTypeComparator,
    nodeStateFactory = nodeStateFactory,
    actionDataSource = actionDataSource,
    nodeDataSource = nodeDataSource,
    iconDataSource = iconDataSource,
    detailsDataSource = detailsDataSource
) {

    override fun onEvent(event: GraphEvent) {
        when (event) {
            is NodeAddEvent -> {
                val node = event.node

                val action = actionDataSource.getActionById(node.getType())

                // Disallow adding event nodes in function graph
                if (action?.type == ActionType.EVENT)
                    return
            }
        }

        super.onEvent(event)
    }

    override val actionFilter: (Action) -> Boolean = { it.type != ActionType.EVENT && super.actionFilter(it) }

}