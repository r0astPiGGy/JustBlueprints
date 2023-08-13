package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.model.ProgramData
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProgramDataRepositoryImpl : ProgramDataRepository {

    private val dirPath = justBlueprintsDirectoryPath
    private val cachePath = "$dirPath${File.separator}ProgramData.json"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun save(data: ProgramData) {
        val jsonData = json.encodeToString(data)

        File(dirPath).mkdirs()
        File(cachePath).apply {
            createNewFile()
            writeText(jsonData)
        }
    }

    override fun load(): ProgramData {
        return try {
            json.decodeFromString(File(cachePath).readText())
        } catch (e: Exception) {
            ProgramData()
        }
    }
}

val justBlueprintsDirectoryPath: String
    get() = "${System.getProperty("user.home")}${File.separator}JustBlueprints"

val projectsPath: String
    get() = "${justBlueprintsDirectoryPath}${File.separator}projects${File.separator}"