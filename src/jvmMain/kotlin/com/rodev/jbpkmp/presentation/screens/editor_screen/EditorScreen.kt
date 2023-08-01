package com.rodev.jbpkmp.presentation.screens.editor_screen

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
import androidx.compose.ui.unit.dp
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.presentation.components.Sheet
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.settings_screen.SettingsScreen
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar

@Composable
fun EditorScreen(navController: NavController, projectPath: String) {
    val viewModel = EditorScreenViewModel()

    val project = Project.loadFromFolder(projectPath)

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

        Sheet(showSettingsScreen) {
            SettingsScreen(
                onDismissRequest = { showSettingsScreen = false },
                modifier = Modifier.padding(75.dp)
            )
        }
    }
}