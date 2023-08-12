package com.rodev.jbpkmp.presentation.screens.settings_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.domain.model.Settings
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.update

class SettingsScreenViewModel(
    val repository: ProgramDataRepository
) {

    var settings by mutableStateOf(repository.load().settings)

    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.SaveSettings -> {
                repository.update {
                    settings.languageCode = event.language
                    settings.useDarkTheme = event.useDarkTheme
                    settings.openLastProject = event.openLastProject
                }.also {
                    settings = it.settings
                }
            }
        }
    }
}