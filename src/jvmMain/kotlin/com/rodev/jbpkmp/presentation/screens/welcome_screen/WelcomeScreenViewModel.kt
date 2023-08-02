package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.RecentProject
import com.rodev.jbpkmp.domain.model.save
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import com.rodev.jbpkmp.domain.repository.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import java.io.File

class WelcomeScreenViewModel(
    private val repository: ProgramDataRepository
) {
    var state by mutableStateOf(WelcomeScreenState())
        private set

    init {
        getRecentProjects()
    }

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            is WelcomeScreenEvent.LoadAndOpenProject -> {
                val projectJson = File(event.path).readText()
                val project = Json.decodeFromString<Project>(projectJson)

                val recentProject = RecentProject(
                    name = project.name,
                    path = project.path,
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )

                repository.update {
                    recentProjects.add(recentProject)
                }

                getRecentProjects()

                openProject(recentProject)
            }

            is WelcomeScreenEvent.CreateAndOpenProject -> {
                val directory = "${event.directory}/${event.name}"

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

                repository.update {
                    recentProjects.add(recentProject)
                }

                getRecentProjects()

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
        updateState { it.copy(
            loadProjectResult = LoadProjectResult.Success(
                projectPath = project.path
            )
        ) }
    }

    fun resetState() {
        updateState {
            it.copy(
                loadProjectResult = null
            )
        }
    }

    private fun updateState(block: (WelcomeScreenState) -> WelcomeScreenState) {
        state = block(state)
    }

    private fun getRecentProjects() {
        val data = repository.load()

        updateState {
            it.copy(
                // Sort
                recentProjects = data.recentProjects.reversed()
            )
        }
    }
}