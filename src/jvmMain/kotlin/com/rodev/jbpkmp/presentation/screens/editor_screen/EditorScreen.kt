package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Details
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DraggableContext
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.DropTarget
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.Overview
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreen

@Composable
fun EditorScreen(navController: NavController, projectPath: String) {
    val viewModel = remember { EditorScreenViewModel(projectPath) }

    // Auto-save
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }

    Surface(
        modifier = Modifier
            .onKeyEvent(viewModel::handleKeyEvent)
    ) {
        var showSettingsScreen by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ToolBar(
                modifier = Modifier.fillMaxWidth(),
                startContent = {
                    // Back button
                    MaterialIconButton(
                        imageVector = Icons.Outlined.ArrowBack,
                        onClick = { navController.navigateBack() }
                    )

                    // Settings button
                    MaterialIconButton(
                        imageVector = Icons.Outlined.Settings,
                        onClick = { showSettingsScreen = true }
                    )
                },
                endContent = {
                    // Build button
                    MaterialIconButton(
                        imageVector = Icons.Outlined.Build,
                        enabled = !viewModel.state.isLoading,
                        onClick = { viewModel.onEvent(EditorScreenEvent.BuildProject) }
                    )

                    // Save button
                    MaterialIconButton(
                        imageVector = Icons.Outlined.Done,
                        enabled = !viewModel.state.isLoading,
                        onClick = { viewModel.onEvent(EditorScreenEvent.SaveProject) }
                    )
                }
            )

            HorizontalDivider()

            DraggableContext {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    VerticalDivider()

                    Column(
                        modifier = Modifier
                            .requiredWidth(300.dp)
                    ) {

                        Overview(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            viewModel = viewModel
                        )

                        HorizontalDivider()

                        Details(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            viewModel = viewModel
                        )
                    }

                    VerticalDivider()

                    val currentGraph = viewModel.currentGraph

                    if (currentGraph != null) {
                        DropTarget<VariableState>(
                            modifier = Modifier
                        ) { isInBound, data, position ->
                            data?.let {
                                if (isInBound) {
                                    viewModel.onEvent(
                                        EditorScreenEvent.OnDragAndDrop(
                                            data,
                                            position
                                        )
                                    )
                                }
                            }

                            ViewPortPreview(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (isInBound) {
                                            Modifier.border(4.dp, color = Color.Blue)
                                        } else {
                                            Modifier
                                        }
                                    ),
                                viewModel = currentGraph.viewModel
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Project is loading",
                                fontSize = 30.sp,
                            )
                        }
                    }
                }
            }

            HorizontalDivider()
        }

        Sheet(showSettingsScreen) {
            SettingsScreen(
                onDismissRequest = { showSettingsScreen = false },
                modifier = Modifier.padding(75.dp)
            )
        }
    }
}

const val dividerSize = 4

@Composable
fun Pane(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Max)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp)
                .verticalScroll(scrollState),
            content = content
        )
    }
}

@Composable
fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(dividerSize.dp),
        color = Color.Black
    )
}

@Composable
fun HorizontalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(dividerSize.dp),
        color = Color.Black
    )
}

@Composable
fun MaterialIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    onClick: () -> Unit = {},
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}