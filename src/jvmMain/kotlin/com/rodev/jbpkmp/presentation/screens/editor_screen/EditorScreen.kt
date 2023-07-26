package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.presentation.navigation.NavController
import com.rodev.jbpkmp.presentation.screens.editor_screen.components.ToolBar

@Composable
fun EditorScreen(navController: NavController) {
    val viewModel = EditorScreenViewModel()

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ToolBar(
                modifier = Modifier.fillMaxWidth(),
                backAction = { navController.navigateBack() },
                settingsAction = {},
                buildAction = { viewModel.onEvent(EditorScreenEvent.BuildProject) },
                saveAction = { viewModel.onEvent(EditorScreenEvent.SaveProject) }
            )
            ViewPortPreview()
        }
    }
}