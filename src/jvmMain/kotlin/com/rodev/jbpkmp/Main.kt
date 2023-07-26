package com.rodev.jbpkmp

import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.singleWindowApplication
import com.rodev.jbpkmp.presentation.localization.Localization
import com.rodev.jbpkmp.presentation.localization.RUSSIAN
import com.rodev.jbpkmp.presentation.localization.appName
import com.rodev.jbpkmp.presentation.navigation.JustBlueprintsNavigationHost
import com.rodev.jbpkmp.presentation.navigation.Screen
import com.rodev.jbpkmp.presentation.navigation.rememberNavController
import com.rodev.jbpkmp.theme.AppTheme

fun main() = singleWindowApplication(
    title = appName
) {
    val navController by rememberNavController(Screen.WelcomeScreen.name)

    AppTheme(useDarkTheme = true) {
        Localization(locale = RUSSIAN) {
            Surface {
                JustBlueprintsNavigationHost(navController)
            }
        }
    }
}