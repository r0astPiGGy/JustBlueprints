package com.rodev.jbpkmp.domain.model

import com.rodev.generator.action.json
import com.rodev.jbpkmp.domain.model.Project.Companion.infoFile
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

        const val infoFile = "project.json"
        const val dataFile = "data.json"
        const val outputFile = "output.json"

        fun loadFromFolder(folderPath: String): Project {
            val folder = File(folderPath)

            require(folder.exists() && folder.isDirectory) {
                "Required path is not exists"
            }

            val info = File(folder, infoFile)

            require(info.isFile && info.exists()) {
                "Project info file is not exists"
            }

            return Json.decodeFromString<Project>(info.readText())
        }
    }
}

fun Project.file(name: String): File {
    val directory = File(path)
    directory.mkdirs()

    return File(directory, name)
}

fun Project.save(json: Json = Json) {
    val projectJson = json.encodeToString(this)

    file(infoFile).writeText(projectJson)
}

fun Project.loadBlueprint(): Blueprint {
    val dataFile = file(Project.dataFile)

    return try {
        val dataJson = dataFile.readText()
        json.decodeFromString(dataJson)
    } catch (e: Exception) {
        emptyBlueprint()
    }
}

fun Project.saveBlueprint(json: Json = Json, blueprint: Blueprint) {
    val dataJson = json.encodeToString(blueprint)

    file(Project.dataFile).writeText(dataJson)
}

fun Project.writeCompileOutput(data: String) {
    file(Project.outputFile).writeText(data)
}