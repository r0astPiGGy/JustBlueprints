package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class ActionDetails(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val additionalInfo: List<String> = emptyList(),
    val worksWith: List<String> = emptyList()
)
