package com.rodev.jbpkmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.node.DefaultNodeStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.DefaultPinStateFactory
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.pin.row.DefaultPinRowStateFactory
import com.rodev.jbpkmp.theme.AppTheme
import com.rodev.jbpkmp.util.randomNode
import com.rodev.nodeui.components.graph.GraphViewModel
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

@Composable
private fun ViewPortPreview() {
    val viewPortModel = remember {
        GraphViewModel(
            nodeStateFactory = DefaultNodeStateFactory(
                pinRowStateFactory = DefaultPinRowStateFactory(
                    pinStateFactory = DefaultPinStateFactory()
                )
            )
        )
    }

    // LaunchedEffect {
    //    viewPortModel.loadFromJson(json)
    // }

    Row {
        Button(onClick = {
            viewPortModel.onEvent(NodeAddEvent(randomNode()))
        }) {
            Text(text = "Add node")
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

    GraphViewPort(
        modifier = Modifier.fillMaxSize(),
        viewModel = viewPortModel,
    ) {
        viewPortModel.nodeStates.forEach {
            // костыль или не костыль? зато пофиксило баг
            key(it.runtimeUUID) {
                it.nodeRepresentation.onDraw(it, viewPortModel, viewPortModel)
            }
        }
    }
}