package com.rodev.jbpkmp.presentation

object ResString {
    const val appName = "JustBlueprints"

    // WelcomeScreen
    val authors: String
    val createNewProject: String
    val openProject: String

    // CreateProjectDialog
    val name: String
    val cancel: String
    val create: String

    init {
        if (System.getProperty("user.language") == "ru") {
            // WelcomeScreen
            authors = "Сделано r0astPiGGy, Dewerro"
            createNewProject = "Создать новый проект"
            openProject = "Открыть проект"

            // CreateProjectDialog
            name = "Название"
            cancel = "Отмена"
            create = "Создать"
        } else {
            // WelcomeScreen
            createNewProject = "Create a new project"
            authors = "Made by r0astPiGGy, Dewerro"
            openProject = "Open a project"

            // CreateProjectDialog
            name = "Name"
            cancel = "Cancel"
            create = "Create"
        }
    }
}