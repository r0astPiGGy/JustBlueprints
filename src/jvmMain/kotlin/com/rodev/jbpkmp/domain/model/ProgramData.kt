package com.rodev.jbpkmp.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ProgramData(
    val projects: MutableSet<Project> = mutableSetOf(),
    val recentProjects: MutableSet<RecentProject> = mutableSetOf(),
    var settings: Settings = Settings()
)

@Serializable
data class Project(
    val name: String,
    val path: String
)

@Serializable
data class RecentProject(
    val project: Project,
    val lastOpeningDate: LocalDate
)

@Serializable
data class Settings(
    var languageCode: String = "ru",
    var useDarkTheme: Boolean = true
)