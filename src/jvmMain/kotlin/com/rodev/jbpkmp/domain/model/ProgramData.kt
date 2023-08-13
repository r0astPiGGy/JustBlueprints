package com.rodev.jbpkmp.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ProgramData(
    var recentProjects: MutableSet<RecentProject> = mutableSetOf(),
    var settings: Settings = Settings(),
    var lastOpenProjectPath: String? = null
)

@Serializable
data class RecentProject(
    val name: String,
    val path: String,
    val lastOpeningDate: LocalDate
)

@Serializable
data class Settings(
    var languageCode: String = "ru",
    var useDarkTheme: Boolean = true,
    var openLastProject: Boolean = false,
    var forceCodeLoad: Boolean = false
)