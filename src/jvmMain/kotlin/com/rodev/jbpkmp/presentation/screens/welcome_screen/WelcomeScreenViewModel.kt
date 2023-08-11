package com.rodev.jbpkmp.presentation.screens.welcome_screen

import com.rodev.jbpkmp.data.ProgramDataRepositoryImpl
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.RecentProject
import com.rodev.jbpkmp.domain.model.save
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File

class WelcomeScreenViewModel(
    private val repository: ProgramDataRepository = ProgramDataRepositoryImpl()
) {
    val state = WelcomeScreenState()
    val projectsPanelState = ProjectsPanelState(
        onProjectOpen = {
            onEvent(WelcomeScreenEvent.OpenProject(it))
        },
        onProjectDelete = {
            onEvent(WelcomeScreenEvent.RemoveProject(it))
        }
    )

    init {
        val settings = repository.load().settings
        val lastOpenProjectPath = settings.lastOpenProjectPath

        if (settings.openLastProject && lastOpenProjectPath != null) {
            if (Project.isValid(lastOpenProjectPath)) {
                state.result = WelcomeScreenResult.OpenProject(lastOpenProjectPath)
            } else {
                repository.update {
                    settings.lastOpenProjectPath = null
                }
            }
        }

        getRecentProjects()
    }

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            is WelcomeScreenEvent.LoadAndOpenProject -> {
                val project = try {
                    Project.loadFromFile(event.path)
                } catch (e: Exception) {
                    state.result = WelcomeScreenResult.Failure(
                        WelcomeScreenResult.Failure.Error.INVALID_PROJECT
                    )
                    return
                }

                val recentProject = RecentProject(
                    name = project.name,
                    path = project.path,
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )

                openProject(recentProject)
            }

            is WelcomeScreenEvent.CreateAndOpenProject -> {
                val directory = "${event.directory}${File.separator}${event.name}"

                val project = Project(
                    name = event.name,
                    path = directory
                )

                val recentProject = RecentProject(
                    name = event.name,
                    path = directory,
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )
                project.save()

                openProject(recentProject)
            }

            is WelcomeScreenEvent.RemoveProject -> {
                repository.update {
                    recentProjects.remove(event.project)
                }

                getRecentProjects()
            }

            is WelcomeScreenEvent.OpenProject -> {
                openProject(event.project)
            }
        }
    }

    private fun openProject(project: RecentProject) {
        state.result = WelcomeScreenResult.OpenProject(project.path)

        repository.update {
            recentProjects.let {
                it.remove(project)
                it.add(project.copy(
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                ))
            }
            settings.lastOpenProjectPath = project.path
        }

        getRecentProjects()
    }

    private fun getRecentProjects() {
        val recentProjects =
            repository.update {
                recentProjects.removeIf { !Project.isValid(it.path) }
            }
            .recentProjects
            .sortedBy(RecentProject::lastOpeningDate) // Sort
            .reversed()

        projectsPanelState.updateProjects(recentProjects)
    }
}