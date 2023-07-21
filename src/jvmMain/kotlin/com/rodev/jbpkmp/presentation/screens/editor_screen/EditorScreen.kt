package com.rodev.jbpkmp.presentation.screens.editor_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rodev.jbpkmp.ViewPortPreview
import com.rodev.jbpkmp.presentation.navigation.NavController

@Composable
fun EditorScreen(navController: NavController) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ViewPortPreview()
        }
    }
}