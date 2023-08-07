package com.rodev.jbpkmp.presentation.screens.settings_screen

sealed class SettingsScreenEvent {
    data class SaveSettings(
        val language: String,
        val useDarkTheme: Boolean,
        val openLastProject: Boolean
    ) : SettingsScreenEvent()
}
