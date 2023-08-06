package com.rodev.jbpkmp.presentation.screens.welcome_screen

import com.rodev.jbpkmp.domain.model.RecentProject

data class WelcomeScreenState(
    val recentProjects: List<RecentProject> = emptyList(),
    val loadProjectResult: LoadProjectResult? = null
)

sealed class LoadProjectResult {
    object Loading : LoadProjectResult()

    class Failure(val msg: String) : LoadProjectResult()

    class Success(val projectPath: String) : LoadProjectResult()

}