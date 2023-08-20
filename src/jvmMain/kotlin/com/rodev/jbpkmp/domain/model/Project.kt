package com.rodev.jbpkmp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val name: String,
    val path: String,
    var editorData: EditorData? = null
)

@Serializable
data class EditorData(
    val selectedTabId: String,
    val openedTabs: Set<EditorTab> = emptySet()
)

@Serializable
sealed interface EditorTab {
    val id: String
}

@Serializable
@SerialName("invokable")
data class InvokableGraphTab(
    override val id: String
) : EditorTab

@Serializable
@SerialName("event")
data class EventGraphTab(
    override val id: String
) : EditorTab