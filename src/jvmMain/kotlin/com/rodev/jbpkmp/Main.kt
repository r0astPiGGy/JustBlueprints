package com.rodev.jbpkmp

import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.singleWindowApplication
import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.presentation.localization.Localization
import com.rodev.jbpkmp.presentation.localization.appName
import com.rodev.jbpkmp.presentation.navigation.JustBlueprintsNavigationHost
import com.rodev.jbpkmp.presentation.navigation.Screen
import com.rodev.jbpkmp.presentation.navigation.rememberNavController
import com.rodev.jbpkmp.theme.AppTheme
import java.util.Locale

typealias LocaleSetter = (Locale) -> Unit
typealias DarkThemeSetter = (Boolean) -> Unit

val LocalMutableLocale = compositionLocalOf<LocaleSetter> { { } }
val LocalMutableTheme = compositionLocalOf<DarkThemeSetter> { { } }

fun main() = singleWindowApplication(
    title = appName
) {
    val navController by rememberNavController(Screen.WelcomeScreen.name)

    val settings = ProgramDataRepositoryImpl().load().settings

    var locale by remember { mutableStateOf(Locale(settings.languageCode)) }
    var useDarkTheme by remember { mutableStateOf(settings.useDarkTheme) }

    AppTheme(useDarkTheme = useDarkTheme) {
        CompositionLocalProvider(
            LocalMutableLocale provides { locale = it },
            LocalMutableTheme provides { useDarkTheme = it }
        ) {
            Localization(locale = locale) {
                Surface {
                    JustBlueprintsNavigationHost(navController)
                }
            }
        }
    }
}