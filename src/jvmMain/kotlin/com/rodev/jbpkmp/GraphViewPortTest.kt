package com.rodev.jbpkmp

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.data.GlobalDataSource
import com.rodev.jbpkmp.presentation.screens.editor_screen.SelectionHandler
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.BlueprintContextMenu
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.row.DefaultPinRowStateFactory
import com.rodev.jbpkmp.theme.AppTheme
import com.rodev.nodeui.components.graph.GraphLayout
import com.rodev.nodeui.components.graph.GraphViewPort
import com.rodev.nodeui.components.graph.NodeClearEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content

fun main() {
    GlobalDataSource.load()

    application {
        Window(onCloseRequest = ::exitApplication) {
            AppTheme(useDarkTheme = true) {
                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ViewPortPreview()
                    }
                }
            }
        }
    }
}

@Composable
fun rememberViewPortViewModel(selectionHandler: SelectionHandler = SelectionHandler) = remember { defaultViewPortViewModel(selectionHandler) }

fun defaultViewPortViewModel(selectionHandler: SelectionHandler) = ViewPortViewModel(
    nodeStateFactory = DefaultNodeStateFactory(
        actionDataSource = GlobalDataSource,
        nodeDataSource = GlobalDataSource,
        nodeTypeDataSource = GlobalDataSource,
        pinRowStateFactory = DefaultPinRowStateFactory(
            pinStateFactory = DefaultPinStateFactory(
                nodeDataSource = GlobalDataSource,
                pinTypeDataSource = GlobalDataSource
            )
        ),
        selectionHandler = selectionHandler
    ),
    pinTypeComparator = DefaultPinTypeComparator,
    actionDataSource = GlobalDataSource,
    nodeDataSource = GlobalDataSource
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewPortPreview(
    modifier: Modifier = Modifier,
    viewModel: ViewPortViewModel = rememberViewPortViewModel()
) {
    BlueprintContextMenu(
        contextMenuModelProvider = { viewModel.contextMenuModel!! },
        onDismiss = {
            viewModel.onEvent(CloseContextMenuGraphEvent)
        },
        onTreeNodeClick = {
            viewModel.onEvent(
                ActionSelectedGraphEvent(it)
            )
        },
        expanded = viewModel.showContextMenu
    )

    GraphViewPort(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    matcher = PointerMatcher
                        .mouse(PointerButton.Secondary)
                ) {
                    viewModel.onEvent(
                        ShowContextMenuGraphEvent(position = it)
                    )
                }
            },
        viewModel = viewModel
    ) {
        GraphLayout(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .drawBehind {
                    viewModel.temporaryLine?.draw(this)
                    viewModel.lines.forEach { it.draw(this) }
                },
        ) {
            nodeStates.forEach {
                // костыль или не костыль? зато пофиксило баг
                key(it.runtimeUUID) {
                    it.nodeRepresentation.onDraw(it, viewModel, viewModel)
                }
            }
        }
    }
}