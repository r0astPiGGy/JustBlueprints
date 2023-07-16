package com.rodev.jbpkmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rodev.jbpkmp.presentation.components.graph.GraphViewModel
import com.rodev.jbpkmp.presentation.components.graph.GraphViewPort
import com.rodev.jbpkmp.presentation.components.graph.NodeAddEvent
import com.rodev.jbpkmp.presentation.components.graph.NodeClearEvent
import com.rodev.jbpkmp.presentation.components.node.Node
import com.rodev.jbpkmp.presentation.components.pin.DefaultPinStateFactory
import com.rodev.jbpkmp.presentation.components.pin.row.DefaultRowStateFactory
import com.rodev.jbpkmp.theme.AppTheme
import com.rodev.jbpkmp.util.randomNode

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
    val viewPortModel = remember { GraphViewModel() }
    val pinRowStateFactory = remember { DefaultRowStateFactory }
    val pinStateFactory = remember { DefaultPinStateFactory }

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
    }

    GraphViewPort(
        modifier = Modifier.fillMaxSize(),
        viewModel = viewPortModel,
    ) {
        nodeStates.forEach {
            Node(it, viewPortModel, viewPortModel, pinRowStateFactory, pinStateFactory)
        }
    }
}