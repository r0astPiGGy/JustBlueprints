package com.rodev.jbpkmp.presentation.navigation

import androidx.compose.runtime.Composable
import com.rodev.jbpkmp.presentation.screens.editor_screen.EditorScreen
import com.rodev.jbpkmp.presentation.screens.welcome_screen.WelcomeScreen

@Composable
fun JustBlueprintsNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.WelcomeScreen.name) {
            WelcomeScreen(navController)
        }

        composable(Screen.EditorScreen.name) {
            EditorScreen(navController, getString("projectPath")!!)
        }
    }.build()
}