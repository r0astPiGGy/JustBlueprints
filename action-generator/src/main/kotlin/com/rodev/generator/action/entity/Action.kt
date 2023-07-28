package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val id: String,
    val name: String,
    val input: Set<String>,
    val output: Set<String>,
    val iconNamespace: String,
    val category: String
)
