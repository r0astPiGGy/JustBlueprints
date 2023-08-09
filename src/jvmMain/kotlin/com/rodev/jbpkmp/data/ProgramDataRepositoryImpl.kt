package com.rodev.jbpkmp.data

import com.rodev.jbpkmp.domain.model.ProgramData
import com.rodev.jbpkmp.domain.repository.ProgramDataRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProgramDataRepositoryImpl : ProgramDataRepository {

    private val dirPath = "${System.getProperty("user.home")}${File.separator}JustBlueprints"
    private val cachePath = "$dirPath${File.separator}ProgramData.json"

    override fun save(data: ProgramData) {
        val json = Json { prettyPrint = true }
        val jsonData = json.encodeToString(data)

        File(dirPath).mkdirs()
        File(cachePath).apply {
            createNewFile()
            writeText(jsonData)
        }
    }

    override fun load(): ProgramData {
        return try {
            Json.decodeFromString(File(cachePath).readText())
        } catch (e: Exception) {
            ProgramData()
        }
    }
}