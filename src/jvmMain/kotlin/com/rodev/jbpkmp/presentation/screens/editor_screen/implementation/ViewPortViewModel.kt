package com.rodev.jbpkmp.presentation.screens.editor_screen.implementation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.rodev.jbpkmp.domain.repository.ActionDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextMenuItemProvider
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextMenuModel
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.ContextTreeNode
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.TreeNodeBuilder
import com.rodev.nodeui.components.graph.GraphEvent
import com.rodev.nodeui.components.graph.GraphViewModel
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.components.graph.PinTypeComparator
import com.rodev.nodeui.components.node.NodeStateFactory
import com.rodev.nodeui.components.pin.PinState
import com.rodev.nodeui.components.wire.WireFactory
import kotlin.random.Random

class ViewPortViewModel(
    pinTypeComparator: PinTypeComparator = PinTypeComparator.Default,
    nodeStateFactory: NodeStateFactory,
    wireFactory: WireFactory = WireFactory(),
    private val actionDataSource: ActionDataSource
) : GraphViewModel(
    nodeStateFactory = nodeStateFactory,
    pinTypeComparator = pinTypeComparator,
    wireFactory = wireFactory
) {

    private var shouldShowContextMenu by mutableStateOf(false)
    val showContextMenu: Boolean
        get() = shouldShowContextMenu

    private var _contextMenuModel by mutableStateOf<ContextMenuModel?>(null)
    val contextMenuModel: ContextMenuModel?
        get() = _contextMenuModel

    private var lastContextMenuInvokePosition: Offset? = null

    override fun onEvent(event: GraphEvent) {
        when (event) {
            is ShowContextMenuGraphEvent -> {
                lastContextMenuInvokePosition = event.position

                _contextMenuModel = ContextMenuModel(
                    borderColor = Color(Random.nextInt(), Random.nextInt(), Random.nextInt()),
                    contextMenuItemProvider = {
                        actionDataSource.getActions<ContextTreeNode>(
                            rootTransformFunction = { category, child ->
                                ContextTreeNode.Root(child, category.name)
                            },
                            leafTransformFunction = {
                                ContextTreeNode.Leaf(name = it.name, id = it.id)
                            }
                        )
                    }
                )
                shouldShowContextMenu = true
            }

            CloseContextMenuGraphEvent -> {
                onContextMenuClose()
            }

            is ActionSelectedGraphEvent -> {
                val (x, y) = lastContextMenuInvokePosition!!

                onEvent(
                    NodeAddEvent(
                        node = actionDataSource.getNodeById(event.treeNode.id).copy(
                            x = x,
                            y = y
                        )
                    )
                )

                onContextMenuClose()
            }

            else -> super.onEvent(event)
        }
    }

    private fun onContextMenuClose() {
        lastContextMenuInvokePosition = null
        shouldShowContextMenu = false
        _contextMenuModel = null
    }

    override fun onPinDragEndWithoutConnection(pinState: PinState) {
        // Show context menu
    }

}

private fun createSampleTree(): List<ContextTreeNode> {
    return TreeNodeBuilder.create {
        root(name = "Функции") {
            root(name = "Разное") {
                leaf("Test4Function", "test4")
            }
            leaf("Test1Function", "test1")
            leaf("TestFunction", "test")
        }
        root(name = "Ивенты") {
            root(name = "Игрок") {
                leaf("Игрок зашёл", "player_join")
                leaf("Игрок вышел", "player_quit")
            }
            root(name = "Моб") {
                leaf("Моб умер", "mob_death")
                leaf("Моб заспавнился", "mob_spawn")
            }
        }
    }
}

object CloseContextMenuGraphEvent : GraphEvent

data class ShowContextMenuGraphEvent(
    val position: Offset
) : GraphEvent

data class ActionSelectedGraphEvent(
    val treeNode: ContextTreeNode.Leaf
) : GraphEvent