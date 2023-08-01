package com.rodev.jbpkmp.presentation.screens.welcome_screen

import com.rodev.jbpkmp.domain.model.RecentProject

sealed class WelcomeScreenEvent {
    data class OpenProject(val project: RecentProject) : WelcomeScreenEvent()
    data class LoadAndOpenProject(val path: String) : WelcomeScreenEvent()
    data class CreateAndOpenProject(val name: String, val directory: String) : WelcomeScreenEvent()
    data class RemoveProject(val project: RecentProject) : WelcomeScreenEvent()
}