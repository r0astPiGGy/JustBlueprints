package com.rodev.jbpkmp.domain.repository

import com.rodev.jbpkmp.domain.model.Project

interface LocalProjectLoader : ProjectLoader {

    fun loadProjectFromFolder(path: String): LocalProjectReference

    fun loadProjectFromFile(file: String): LocalProjectReference

    fun isValidFolder(folderPath: String): Boolean

    override fun referenceOf(project: Project): LocalProjectReference

}

interface LocalProjectReference : ProjectReference {
    fun writeData(fileName: String, data: String)

}