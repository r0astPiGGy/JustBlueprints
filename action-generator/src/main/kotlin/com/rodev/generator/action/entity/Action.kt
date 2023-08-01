package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: String,
    val name: String,
    val input: Set<String>,
    val output: Set<String>,
    val iconPath: String,
    val category: String
)

fun iconPathFrom(namespace: String, id: String) = iconDirectoryFrom(namespace) + id + ".png"

fun iconDirectoryFrom(namespace: String) = "images/icons/$namespace/"