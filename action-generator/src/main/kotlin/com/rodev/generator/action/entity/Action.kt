package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: String,
    val name: String,
    val input: Set<String>,
    val output: Set<String>,
    val iconPath: String,
    val category: String,
    val type: ActionType
)

fun iconPathFrom(namespace: String, id: String) = iconDirectoryFrom(namespace) + id + ".png"

fun iconDirectoryFrom(namespace: String) = "images/icons/$namespace/"

enum class ActionType {
    DEFAULT,
    EVENT,
    GAME_VALUE_GETTER,
    NATIVE,
    FACTORY,
    @Deprecated(message = "Use Action#hidden instead. (TODO)")
    HIDDEN,
    DECLARATION
}