package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.mutableStateListOf
import com.rodev.jbpkmp.domain.model.RecentProject

class ProjectsPanelState {

    val projects = mutableStateListOf<RecentProjectState>()

    private var selectedProject: RecentProjectState? = null

    fun onSelect(recentProjectState: RecentProjectState) {
        selectedProject?.selected = false
        recentProjectState.selected = true
        selectedProject = recentProjectState
    }

    fun isProjectSelected(project: RecentProjectState): Boolean {
        return selectedProject == project
    }

    fun updateProjects(recentProjects: List<RecentProject>) {
        projects.clear()
        recentProjects.map(::RecentProjectState).let(projects::addAll)
    }

    fun onDelete(recentProjectState: RecentProjectState) {
        if (recentProjectState == selectedProject) {
            selectedProject = null
        }
    }

}