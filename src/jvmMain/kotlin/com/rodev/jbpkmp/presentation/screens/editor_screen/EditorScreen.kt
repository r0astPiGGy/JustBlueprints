package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.SettingsScreen
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar

@Composable
fun EditorScreen(navController: NavController) {
    val viewModel = EditorScreenViewModel()

    Surface {
        var showSettingsScreen by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ToolBar(
                modifier = Modifier.fillMaxWidth(),
                backAction = { navController.navigateBack() },
                settingsAction = { showSettingsScreen = true },
                buildAction = { viewModel.onEvent(EditorScreenEvent.BuildProject) },
                saveAction = { viewModel.onEvent(EditorScreenEvent.SaveProject) }
            )

            ViewPortPreview()
        }

        AnimatedVisibility(showSettingsScreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                SettingsScreen(
                    onDismissRequest = { showSettingsScreen = false },
                    modifier = Modifier.padding(75.dp)
                )
            }
        }
    }
}