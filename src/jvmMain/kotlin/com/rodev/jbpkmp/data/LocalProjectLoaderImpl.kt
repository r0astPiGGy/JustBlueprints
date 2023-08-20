package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.model.*
import com.rodev.jbpkmp.domain.repository.BlueprintReference
import com.rodev.jbpkmp.domain.repository.LocalProjectReference
import com.rodev.jbpkmp.domain.repository.LocalProjectLoader
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LocalProjectLoaderImpl(
    private val json: Json
) : LocalProjectLoader {
    override fun loadProjectFromFile(file: String): LocalProjectReference {
        return loadProjectFromFile(File(file))
    }

    override fun referenceOf(project: Project): LocalProjectReference {
        return LocalProjectReferenceImpl(project)
    }

    override fun loadProjectFromFolder(path: String): LocalProjectReference {
        val folder = File(path)

        require(folder.exists() && folder.isDirectory) {
            "Required path doesn't exists"
        }

        val info = File(folder, infoFile)

        return loadProjectFromFile(info)
    }

    private fun loadProjectFromFile(info: File): LocalProjectReference {
        require(info.isFile && info.exists()) {
            "Project info file doesn't exists"
        }

        val project = Json.decodeFromString<Project>(info.readText())

        return referenceOf(project)
    }

    override fun isValidFolder(folderPath: String): Boolean {
        val folder = File(folderPath)

        if (!folder.exists() || !folder.isDirectory) return false

        val info = File(folder, infoFile)

        return info.isFile && info.exists()
    }

    private fun Project.file(name: String): File {
        val directory = File(path)
        directory.mkdirs()

        return File(directory, name)
    }

    private inner class LocalProjectReferenceImpl(
        override val project: Project,
    ) : LocalProjectReference {
        override val blueprint = BlueprintReferenceImpl(project)

        override fun writeData(fileName: String, data: String) {
            project.file(fileName).writeText(data)
        }

        override fun save() {
            writeData(infoFile, json.encodeToString(project))
        }
    }

    private inner class BlueprintReferenceImpl(
        val project: Project
    ) : BlueprintReference {
        override fun save(blueprint: Blueprint) {
            val dataJson = json.encodeToString(blueprint)

            project.file(dataFile).writeText(dataJson)
        }

        override fun load(): Blueprint {
            val dataFile = project.file(dataFile)

            if (!dataFile.exists()) return emptyBlueprint()

            val dataJson = dataFile.readText()

            return try {
                json.decodeFromString(dataJson)
            } catch (e: Exception) {
                json.decodeFromString<LegacyBlueprint>(dataJson).toBlueprint()
            }
        }
    }

    companion object {
        const val infoFile = "project.json"
        const val dataFile = "data.json"
        const val outputFile = "output.json"
    }
}