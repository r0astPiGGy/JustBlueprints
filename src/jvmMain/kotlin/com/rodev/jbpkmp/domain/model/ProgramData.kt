package com.rodev.jbpkmp.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ProgramData(
    val projects: MutableSet<Project> = mutableSetOf(),
    val recentProjects: MutableSet<RecentProject> = mutableSetOf()
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