package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rodev.generator.action.entity.Action
import com.rodev.generator.action.entity.ActionType
import com.rodev.jbpkmp.domain.compiler.Nodes
import com.rodev.jbpkmp.domain.source.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DetailsPanel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextMenuModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextTreeNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.TooltipComposable
import com.rodev.nodeui.components.graph.GraphEvent
import com.rodev.nodeui.components.graph.GraphViewModel
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.wire.WireFactory
import com.rodev.nodeui.model.Node
import kotlin.random.Random

open class ViewPortViewModel(
    pinTypeComparator: PinTypeComparator,
    nodeStateFactory: NodeStateFactory,
    private val actionDataSource: ActionDataSource,
    private val detailsDataSource: ActionDetailsDataSource,
    private val nodeDataSource: NodeDataSource,
    private val iconDataSource: IconDataSource
) : GraphViewModel(
    nodeStateFactory = nodeStateFactory,
    pinTypeComparator = pinTypeComparator
) {

    var showContextMenu by mutableStateOf(false)
        private set

    var contextMenuModel by mutableStateOf<ContextMenuModel?>(null)
        private set

    var lastCursorPosition = Offset.Zero

    private var lastContextMenuInvokePosition: Offset? = null

    override fun onEvent(event: GraphEvent) {
        when (event) {
            is ShowContextMenuGraphEvent -> {
                lastContextMenuInvokePosition = event.position + scrollOffset

                contextMenuModel = ContextMenuModel(
                    borderColor = Color(Random.nextInt(), Random.nextInt(), Random.nextInt()),
                    contextMenuItemProvider = ::provideContextMenuData
                )
                showContextMenu = true
            }

            CloseContextMenuGraphEvent -> {
                onContextMenuClose()
            }

            is NodeAddAtCursorEvent -> {
                val position = lastCursorPosition + scrollOffset

                onEvent(
                    NodeAddEvent(
                        event.node.copy(
                            x = position.x,
                            y = position.y
                        )
                    )
                )
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
                val (x, y) = event.position + scrollOffset
                val variable = event.variable

                onEvent(
                    NodeAddEvent(
                        node = variable.toNode().copy(x = x, y = y)
                    )
                )
            }

            is CreateFunctionGraphEvent -> {
                val (x, y) = event.position + scrollOffset
                val functionState = event.function

                val node = nodeDataSource
                    .getNodeById(Nodes.Type.FUNCTION_REFERENCE)
                    .setInvokableId(functionState.id)

                onEvent(
                    NodeAddEvent(
                        node = node.copy(x = x, y = y)
                    )
                )
            }

            is CreateProcessGraphEvent -> {
                val (x, y) = event.position + scrollOffset
                val processState = event.process

                val node = nodeDataSource
                    .getNodeById(Nodes.Type.PROCESS_REFERENCE)
                    .setInvokableId(processState.id)

                onEvent(
                    NodeAddEvent(
                        node = node.copy(x = x, y = y)
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

    protected open val actionFilter: (Action) -> Boolean = { it.type != ActionType.HIDDEN }

    private fun provideContextMenuData(): List<ContextTreeNode> {
        return actionDataSource.transformActions<ContextTreeNode>(
            filter = actionFilter,
            rootTransformFunction = { category, child ->
                ContextTreeNode.Root(child, category.name)
            },
            leafTransformFunction = {
                val details = detailsDataSource.getActionDetailsById(it.id)
                val tooltipComposable: TooltipComposable? = details?.let {
                    {
                        DetailsPanel(it)
                    }
                }

                ContextTreeNode.Leaf(
                    name = it.name,
                    id = it.id,
                    tooltipComposable = tooltipComposable,
                    iconProvider = {
                        iconDataSource.getIconById(it.iconPath)
                    }
                )
            }
        )
    }

}

object CloseContextMenuGraphEvent : GraphEvent

data class NodeAddAtCursorEvent(
    val node: Node
) : GraphEvent

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

data class CreateProcessGraphEvent(
    val process: ProcessState,
    val position: Offset
) : GraphEvent

data class CreateFunctionGraphEvent(
    val function: FunctionState,
    val position: Offset
) : GraphEvent