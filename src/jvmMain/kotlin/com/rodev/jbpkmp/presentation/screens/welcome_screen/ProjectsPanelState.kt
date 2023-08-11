package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.domain.model.RecentProject

class ProjectsPanelState(
    val onProjectOpen: (RecentProject) -> Unit,
    val onProjectDelete: (RecentProject) -> Unit
) {

    val projects = mutableStateListOf<RecentProjectState>()

    private var selectedProject: RecentProjectState? = null

    fun onSelect(recentProjectState: RecentProjectState) {
        if (selectedProject == recentProjectState) {
            onProjectOpen(recentProjectState.recentProject)
            return
        }

        selectedProject?.selected = false
        recentProjectState.selected = true
        selectedProject = recentProjectState
    }

    fun updateProjects(recentProjects: List<RecentProject>) {
        projects.clear()
        recentProjects.map(::RecentProjectState).let(projects::addAll)
    }

    fun onDelete(recentProjectState: RecentProjectState) {
        onProjectDelete(recentProjectState.recentProject)
        selectedProject = null
    }

}