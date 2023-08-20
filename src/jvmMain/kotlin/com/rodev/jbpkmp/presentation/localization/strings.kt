package com.rodev.jbpkmp.presentation.localization

import com.rodev.jbpkmp.domain.model.variable.Variable

const val appName = "JustBlueprints"

// WelcomeScreen
val authors = Translatable(
    "Сделано r0astPiGGy, Dewerro",
    hashMapOf(
        ENGLISH to "Made by r0astPiGGy, Dewerro"
    )
)
val createNewProject = Translatable(
    "Создать новый проект",
    hashMapOf(
        ENGLISH to "Create a new project"
    )
)
val openProject = Translatable(
    "Открыть проект",
    hashMapOf(
        ENGLISH to "Open a project"
    )
)
val chooseFile = Translatable(
    "Выберите файл",
    hashMapOf(
        ENGLISH to "Choose a file"
    )
)
val noRecentProjects = Translatable(
    "Нет недавних проектов",
    hashMapOf(
        ENGLISH to "No recent projects"
    )
)
val invalidProject = Translatable(
    "Проект недействителен",
    hashMapOf(
        ENGLISH to "Invalid project"
    )
)

// CreateProjectDialog
val chooseDirectory = Translatable(
    "Выберите директорию",
    hashMapOf(
        ENGLISH to "Choose a directory"
    )
)

// UploadScreen
val uploadSuccess = Translatable(
    "Код был загружен на сервер",
    hashMapOf(
        ENGLISH to "The code was uploaded to the server"
    )
)
val uploadHint = Translatable(
    "Код хранится временно, успейте использовать команду",
    hashMapOf(
        ENGLISH to "The code is stored temporarily, have time to use the command"
    )
)
val forceProperty = Translatable(
    "Force",
    hashMapOf(
        ENGLISH to "Force"
    )
)
val forcePropertyTooltip = Translatable(
    "Если выбрано, удаляет предыдущий код при использовании команды на сервере",
    hashMapOf(
        ENGLISH to "If selected, deletes the previous code when using the command on the server"
    )
)

// ErrorScreen
val uploadError = Translatable(
    "При загрузке кода произошла ошибка",
    hashMapOf(
        ENGLISH to "An error occurred during code upload"
    )
)
val compileError = Translatable(
    "При компиляции кода произошла ошибка",
    hashMapOf(
        ENGLISH to "An error occurred during code compile"
    )
)
val loadError = Translatable(
    "При загрузке проекта произошла ошибка",
    hashMapOf(
        ENGLISH to "An error occurred during project loading"
    )
)
val runtimeError = Translatable(
    "Произошла неизвестная ошибка времени выполнения",
    hashMapOf(
        ENGLISH to "An unknown runtime error occurred"
    )
)
val saveError = Translatable(
    "При сохранении произошла ошибка",
    hashMapOf(
        ENGLISH to "An error occurred during save process"
    )
)

// SettingsScreen
val language = Translatable(
    "Язык",
    hashMapOf(
        ENGLISH to "Language"
    )
)
val languageDescription = Translatable(
    "Сообщите нам, какой язык вам удобнее использовать. Вы можете изменить его обратно в любое время.",
    hashMapOf(
        ENGLISH to "Let us know which language you're comfortable using. You can change it back at any time."
    )
)
val useDarkTheme = Translatable(
    "Использовать темную тему",
    hashMapOf(
        ENGLISH to "Use dark theme"
    )
)
var openLastProject = Translatable(
    "Открывать последний редактируемый проект при запуске",
    hashMapOf(
        ENGLISH to "Opens the last edited project on startup"
    )
)
val save = Translatable(
    "Сохранить",
    hashMapOf(
        ENGLISH to "Save"
    )
)

// CreateVariableDialog
val value = Translatable(
    "Значение",
    hashMapOf(
        ENGLISH to "Value"
    )
)

// EditorScreen
fun Localization.variableType(type: Variable.Type): String {
    return when(type) {
        Variable.Type.Local -> localVariable()
        Variable.Type.Game -> gameVariable()
        Variable.Type.Save -> savedVariable()
    }
}

val localVariable = Translatable(
    "Локальная переменная",
    hashMapOf(
        ENGLISH to "Local variable"
    )
)
val gameVariable = Translatable(
    "Игровая переменная",
    hashMapOf(
        ENGLISH to "Game variable"
    )
)
val savedVariable = Translatable(
    "Сохраненная переменная",
    hashMapOf(
        ENGLISH to "Saved variable"
    )
)
val localVariables = Translatable(
    "Локальные переменные",
    hashMapOf(
        ENGLISH to "Local variables"
    )
)
val functions = Translatable(
    "Функции",
    hashMapOf(
        ENGLISH to "Functions"
    )
)
val processes = Translatable(
    "Процессы",
    hashMapOf(
        ENGLISH to "Processes"
    )
)
val globalVariables = Translatable(
    "Глобальные переменные",
    hashMapOf(
        ENGLISH to "Global variables"
    )
)
val variableName = Translatable(
    "Имя переменной",
    hashMapOf(
        ENGLISH to "Variable name"
    )
)
val description = Translatable(
    "Описание",
    hashMapOf(
        ENGLISH to "Description"
    )
)
val additionally = Translatable(
    "Дополнительно",
    hashMapOf(
        ENGLISH to "Additional info"
    )
)
val worksWith = Translatable(
    "Работает с:",
    hashMapOf(
        ENGLISH to "Works with:"
    )
)
val projectSave = Translatable(
    "Сохранение проекта...",
    hashMapOf(
        ENGLISH to "Saving project..."
    )
)
val projectCompile = Translatable(
    "Компиляция блюпринта...",
    hashMapOf(
        ENGLISH to "Compiling blueprint..."
    )
)
val codeUpload = Translatable(
    "Загрузка кода...",
    hashMapOf(
        ENGLISH to "Uploading..."
    )
)
val projectLoading = Translatable(
    "Загрузка проекта...",
    hashMapOf(
        ENGLISH to "Loading project..."
    )
)
val addPin = Translatable(
    "Добавить Pin",
    hashMapOf(
        ENGLISH to "Add Pin"
    )
)

// Shared
val cancel = Translatable(
    "Отмена",
    hashMapOf(
        ENGLISH to "Cancel"
    )
)
val create = Translatable(
    "Создать",
    hashMapOf(
        ENGLISH to "Create"
    )
)
val errorMessage = Translatable(
    "Название не может быть пустым",
    hashMapOf(
        ENGLISH to "Name cannot be empty"
    )
)
val ok = Translatable(
    "Окей",
    hashMapOf(
        ENGLISH to "Okay"
    )
)
val copyButton = Translatable(
    "Скопировать",
    hashMapOf(
        ENGLISH to "Copy"
    )
)
val name = Translatable(
    "Название",
    hashMapOf(
        ENGLISH to "Name"
    )
)