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
    surface = gray
)

private val DarkColors = darkColors(
    primary = blue,
    onPrimary = Color.White,
    secondary = cyan,
    background = gray,
    surface = black
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
        content = content
    )
}