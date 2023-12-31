package com.rodev.jbpkmp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = blue,
    onPrimary = Color.White,
    secondary = cyan,
    background = gray,
    onBackground = Color.White,
    surface = Color.White,
    onSurface = black
)

private val DarkColors = darkColors(
    primary = blue,
    onPrimary = Color.White,
    secondary = cyan,
    background = gray,
    onBackground = Color.White,
    surface = black,
    onSurface = Color.White
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}