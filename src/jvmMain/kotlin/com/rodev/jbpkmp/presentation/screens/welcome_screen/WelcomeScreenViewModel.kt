package com.rodev.jbpkmp.presentation.screens.welcome_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.rodev.jbpkmp.domain.model.Project
import com.rodev.jbpkmp.domain.model.RecentProject
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class WelcomeScreenViewModel(
    private val repository: ProgramDataRepository
) {
    private val _state = mutableStateOf(WelcomeScreenState())
    val state: State<WelcomeScreenState>
        get() = _state

    init {
        getRecentProjects()
    }

    fun onEvent(event: WelcomeScreenEvent) {
        when (event) {
            is WelcomeScreenEvent.LoadProject -> {
                val projectJson = File(event.path).readText()
                val project = Json.decodeFromString<Project>(projectJson)
                val recentProject = RecentProject(
                    project = project,
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )

                val programData = repository.load()
                programData.projects.add(project)
                programData.recentProjects.add(recentProject)

                repository.save(programData)

                getRecentProjects()
            }

            is WelcomeScreenEvent.CreateProject -> {
                val directory = "${event.directory}/${event.name}"
                val filePath = "$directory/${event.name}.json"

                val project = Project(
                    name = event.name, path = directory
                )
                val recentProject = RecentProject(
                    project = project,
                    lastOpeningDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )

                val json = Json { prettyPrint = true }
                val projectJson = json.encodeToString(project)
                File(directory).mkdirs()
                File(filePath).apply {
                    createNewFile()
                    writeText(projectJson)
                }

                val programData = repository.load()
                programData.projects.add(project)
                programData.recentProjects.add(recentProject)

                repository.save(programData)

                getRecentProjects()
            }

            is WelcomeScreenEvent.RemoveProject -> {
                val programData = repository.load()
                programData.recentProjects.remove(event.project)

                repository.save(programData)

                getRecentProjects()
            }
        }
    }

    private fun getRecentProjects() {
        val data = repository.load()
        _state.value = state.value.copy(recentProjects = data.recentProjects.reversed())
    }
}