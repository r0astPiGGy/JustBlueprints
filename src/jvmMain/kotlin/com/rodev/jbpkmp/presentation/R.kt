package com.rodev.jbpkmp.presentation

object ResString {
    const val appName = "JustBlueprints"

    // WelcomeScreen
    val authors: String
    val createNewProject: String
    val openProject: String
    val chooseFile: String
    val noRecentProjects: String

    // CreateProjectDialog
    val name: String
    val cancel: String
    val create: String
    val errorMessage: String
    val chooseDirectory: String

    init {
        if (System.getProperty("user.language") == "ru") {
            // WelcomeScreen
            authors = "Сделано r0astPiGGy, Dewerro"
            createNewProject = "Создать новый проект"
            openProject = "Открыть проект"
            chooseFile = "Выберите файл"
            noRecentProjects = "Нет недавних проектов"

            // CreateProjectDialog
            name = "Название"
            cancel = "Отмена"
            create = "Создать"
            errorMessage = "Название не может быть пустым"
            chooseDirectory = "Выберите директорию"
        } else {
            // WelcomeScreen
            createNewProject = "Create a new project"
            authors = "Made by r0astPiGGy, Dewerro"
            openProject = "Open a project"
            chooseFile = "Choose a file"
            noRecentProjects = "No recent projects"

            // CreateProjectDialog
            name = "Name"
            cancel = "Cancel"
            create = "Create"
            errorMessage = "Name cannot be empty"
            chooseDirectory = "Choose a directory"
        }
    }
}