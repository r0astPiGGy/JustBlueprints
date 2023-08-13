package com.rodev.jbpkmp.presentation.screens.editor_screen.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.context_menu.BlueprintContextMenu
import com.rodev.jbpkmp.presentation.screens.editor_screen.implementation.*
import com.rodev.nodeui.components.graph.GraphLayout
import com.rodev.nodeui.components.graph.GraphViewPort

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BlueprintViewPort(
    modifier: Modifier = Modifier,
    viewModel: ViewPortViewModel
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
            .onPointerEvent(PointerEventType.Move) {
                viewModel.lastCursorPosition = it.changes.first().position
            }
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
                    viewModel.temporaryWire?.draw(this)
                    viewModel.wires.forEach { it.draw(this) }
                },
        ) {
            nodeStates.forEach {
                // костыль или не костыль? зато пофиксило баг
                key(it.runtimeUUID) {
                    it.NodeView()
                }
            }
        }
    }
}