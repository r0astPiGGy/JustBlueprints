package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreen
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar
import com.rodev.jbpkmp.rememberViewPortViewModel

@Composable
fun EditorScreen(navController: NavController, projectPath: String) {
    val viewModel = remember { EditorScreenViewModel(projectPath) }

    // Auto-save
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }

    val screenState = viewModel.state

    Surface {
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
                        enabled = !screenState.isLoading,
                        onClick = { viewModel.onEvent(EditorScreenEvent.BuildProject) }
                    )

                    // Save button
                    MaterialIconButton(
                        imageVector = Icons.Outlined.Done,
                        enabled = !screenState.isLoading,
                        onClick = { viewModel.onEvent(EditorScreenEvent.SaveProject) }
                    )
                }
            )

            val currentGraph = viewModel.currentGraph

            if (currentGraph != null) {
                ViewPortPreview(
                    modifier = Modifier
                        .fillMaxSize(),
                    viewModel = currentGraph.viewModel
                )
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

        Sheet(showSettingsScreen) {
            SettingsScreen(
                onDismissRequest = { showSettingsScreen = false },
                modifier = Modifier.padding(75.dp)
            )
        }
    }
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