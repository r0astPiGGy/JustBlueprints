package com.rodev.jbpkmp.presentation.screens.welcome_screen

import com.rodev.jbpkmp.domain.model.RecentProject

data class WelcomeScreenState(
    val recentProjects: List<RecentProject> = emptyList()
)