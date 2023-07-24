package com.rodev.jbpkmp

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.data.ActionDataSourceImpl
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.BlueprintContextMenu
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.*
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.row.DefaultPinRowStateFactory
import com.rodev.jbpkmp.theme.AppTheme
import com.rodev.jbpkmp.util.randomNode
import com.rodev.nodeui.components.graph.GraphViewPort
import com.rodev.nodeui.components.graph.NodeAddEvent
import com.rodev.nodeui.components.graph.NodeClearEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() = application {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewPortPreview() {
    val viewPortModel = remember {
        ViewPortViewModel(
            nodeStateFactory = DefaultNodeStateFactory(
                pinRowStateFactory = DefaultPinRowStateFactory(
                    pinStateFactory = DefaultPinStateFactory()
                )
            ),
            pinTypeComparator = DefaultPinTypeComparator,
            actionDataSource = ActionDataSourceImpl()
        )
    }

    Row {

        Button(onClick = {
            viewPortModel.onEvent(NodeAddEvent(randomNode()))
        }) {
            Text(text = "Add random node")
        }

        Button(onClick = {
            viewPortModel.onEvent(NodeClearEvent)
        }) {
            Text(text = "Clear")
        }

        Button(onClick = {
            val graph = viewPortModel.save()

            val json = Json { prettyPrint = true }

            println(json.encodeToString(graph))
            viewPortModel.load(graph)
        }) {
            Text(text = "Save and Load")
        }
    }

    BlueprintContextMenu(
        contextMenuModelProvider = { viewPortModel.contextMenuModel!! },
        onDismiss = {
            viewPortModel.onEvent(CloseContextMenuGraphEvent)
        },
        onTreeNodeClick = {
            viewPortModel.onEvent(
                ActionSelectedGraphEvent(it)
            )
        },
        expanded = viewPortModel.showContextMenu
    )

    GraphViewPort(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    matcher = PointerMatcher
                        .mouse(PointerButton.Secondary)
                ) {
                    viewPortModel.onEvent(
                        ShowContextMenuGraphEvent(position = it)
                    )
                }
            },
        viewModel = viewPortModel,
        graphModifier = Modifier
            .background(MaterialTheme.colors.surface)
    ) {
        viewPortModel.nodeStates.forEach {
            // костыль или не костыль? зато пофиксило баг
            key(it.runtimeUUID) {
                it.nodeRepresentation.onDraw(it, viewPortModel, viewPortModel)
            }
        }
    }
}