package com.rodev.jbpkmp

import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rodev.jbpkmp.di.appModule
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.presentation.localization.Localization
import com.rodev.jbpkmp.presentation.localization.appName
import com.rodev.jbpkmp.presentation.navigation.JustBlueprintsNavigationHost
import com.rodev.jbpkmp.presentation.navigation.Screen
import com.rodev.jbpkmp.presentation.navigation.rememberNavController
import com.rodev.jbpkmp.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import java.util.Locale

typealias LocaleSetter = (Locale) -> Unit
typealias DarkThemeSetter = (Boolean) -> Unit

val LocalMutableLocale = compositionLocalOf<LocaleSetter> { { } }
val LocalMutableTheme = compositionLocalOf<DarkThemeSetter> { { } }

fun main() = singleWindowApplication(
    title = appName,
    icon = BitmapPainter(useResource("images/logo.png", ::loadImageBitmap)),
    state = WindowState(placement = WindowPlacement.Maximized)
) {
    KoinApplication(
        application = {
            modules(appModule())
        }
    ) {
        val navController by rememberNavController(Screen.WelcomeScreen.name)

        val repository = koinInject<ProgramDataRepository>()
        val programData = remember { repository.load() }

        var locale by remember { mutableStateOf(Locale(programData.settings.languageCode)) }
        var useDarkTheme by remember { mutableStateOf(programData.settings.useDarkTheme) }

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
}
