package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import com.rodev.jbpkmp.domain.repository.NodeDataSource
import com.rodev.jbpkmp.domain.repository.getNodeById
import com.rodev.jbpkmp.presentation.screens.editor_screen.VariableState
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextMenuModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextTreeNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.toNode
import com.rodev.nodeui.components.graph.GraphEvent
import com.rodev.nodeui.components.graph.GraphViewModel
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.wire.WireFactory
import kotlin.random.Random

open class ViewPortViewModel(
    pinTypeComparator: PinTypeComparator = PinTypeComparator.Default,
    nodeStateFactory: NodeStateFactory,
    wireFactory: WireFactory = WireFactory(),
    private val actionDataSource: ActionDataSource,
    private val nodeDataSource: NodeDataSource
) : GraphViewModel(
    nodeStateFactory = nodeStateFactory,
    pinTypeComparator = pinTypeComparator,
    wireFactory = wireFactory
) {

    var showContextMenu by mutableStateOf(false)
        private set

    var contextMenuModel by mutableStateOf<ContextMenuModel?>(null)
        private set

    private var lastContextMenuInvokePosition: Offset? = null

    override fun onEvent(event: GraphEvent) {
        when (event) {
            is ShowContextMenuGraphEvent -> {
                lastContextMenuInvokePosition = event.position

                contextMenuModel = ContextMenuModel(
                    borderColor = Color(Random.nextInt(), Random.nextInt(), Random.nextInt()),
                    contextMenuItemProvider = ::provideContextMenuData
                )
                showContextMenu = true
            }

            CloseContextMenuGraphEvent -> {
                onContextMenuClose()
            }

            is ActionSelectedGraphEvent -> {
                val (x, y) = lastContextMenuInvokePosition!!

                onEvent(
                    NodeAddEvent(
                        node = nodeDataSource.getNodeById(event.treeNode.id).copy(
                            x = x,
                            y = y
                        )
                    )
                )

                onContextMenuClose()
            }

            is CreateVariableGraphEvent -> {
                val (x, y) = event.position
                val variable = event.variable

                onEvent(
                    NodeAddEvent(
                        node = variable.toNode().copy(x = x, y = y)
                    )
                )
            }

            else -> super.onEvent(event)
        }
    }

    private fun onContextMenuClose() {
        lastContextMenuInvokePosition = null
        showContextMenu = false
        contextMenuModel = null
    }

    override fun onPinDragEndWithoutConnection(pinState: PinState) {
        // Show context menu
    }

    open fun provideContextMenuData(): List<ContextTreeNode> {
        return actionDataSource.getActions<ContextTreeNode>(
            rootTransformFunction = { category, child ->
                ContextTreeNode.Root(child, category.name)
            },
            leafTransformFunction = {
                ContextTreeNode.Leaf(name = it.name, id = it.id) {
                    GlobalDataSource.getIconById(it.iconPath)
                }
            }
        )
    }

}

object CloseContextMenuGraphEvent : GraphEvent

data class ShowContextMenuGraphEvent(
    val position: Offset
) : GraphEvent

data class ActionSelectedGraphEvent(
    val treeNode: ContextTreeNode.Leaf
) : GraphEvent

data class CreateVariableGraphEvent(
    val variable: VariableState,
    val position: Offset
) : GraphEvent