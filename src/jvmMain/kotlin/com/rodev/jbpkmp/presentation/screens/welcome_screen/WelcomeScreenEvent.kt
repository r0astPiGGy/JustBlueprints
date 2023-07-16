package com.rodev.jbpkmp.presentation.screens.welcome_screen

import com.rodev.jbpkmp.domain.model.RecentProject

sealed class WelcomeScreenEvent {
    data class LoadProject(val path: String) : WelcomeScreenEvent()
    data class CreateProject(val name: String, val path: String) : WelcomeScreenEvent()
    data class RemoveProject(val project: RecentProject) : WelcomeScreenEvent()
}