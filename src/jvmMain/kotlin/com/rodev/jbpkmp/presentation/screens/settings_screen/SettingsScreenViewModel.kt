package com.rodev.jbpkmp.presentation.screens.settings_screen

import com.rodev.jbpkmp.domain.repository.ProgramDataRepository

class SettingsScreenViewModel(
    private val repository: ProgramDataRepository
) {
    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.SaveSettings -> {
                val data = repository.load().apply {
                    settings.languageCode = event.language
                    settings.useDarkTheme = event.useDarkTheme
                }

                repository.save(data)
            }
        }
    }
}