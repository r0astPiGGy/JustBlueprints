package com.rodev.jbpkmp.domain.model

import com.rodev.jbpkmp.domain.model.Project.Companion.projectInfoFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Project(
    val name: String,
    val path: String
) {

    companion object {

        const val projectInfoFile = "project.json"

        fun loadFromFolder(folderPath: String): Project {
            val folder = File(folderPath)

            require(folder.exists() && folder.isDirectory) {
                "Required path is not exists"
            }

            val info = File(folder, projectInfoFile)

            require(info.isFile && info.exists()) {
                "Project info file is not exists"
            }

            return Json.decodeFromString<Project>(info.readText())
        }
    }
}

fun Project.save() {
    val json = Json { prettyPrint = true }
    val projectJson = json.encodeToString(this)

    val directory = File(path)
    directory.mkdirs()

    File(directory, projectInfoFile).apply {
        createNewFile()
        writeText(projectJson)
    }
}