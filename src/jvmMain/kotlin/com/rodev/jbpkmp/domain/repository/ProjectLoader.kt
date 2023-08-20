package com.rodev.jbpkmp.domain.repository

import com.rodev.jbpkmp.domain.model.Blueprint
import com.rodev.jbpkmp.domain.model.EditorData
import com.rodev.jbpkmp.domain.model.Project

interface ProjectLoader {

    fun referenceOf(project: Project): ProjectReference

}

interface BlueprintReference {

    fun save(blueprint: Blueprint)

    fun load(): Blueprint

}

interface ProjectReference {

    val project: Project
    val blueprint: BlueprintReference

    fun save()
}

fun ProjectReference.updateEditorData(updateFunction: (EditorData?) -> EditorData) {
    project.editorData = updateFunction(project.editorData)
    save()
}