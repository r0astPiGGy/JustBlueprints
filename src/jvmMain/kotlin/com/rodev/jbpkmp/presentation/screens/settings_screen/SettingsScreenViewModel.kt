package com.rodev.jbpkmp.presentation.screens.settings_screen

import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.update

class SettingsScreenViewModel(
    val repository: ProgramDataRepository
) {
    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.SaveSettings -> {
                repository.update {
                    settings.languageCode = event.language
                    settings.useDarkTheme = event.useDarkTheme
                }
            }
        }
    }
}