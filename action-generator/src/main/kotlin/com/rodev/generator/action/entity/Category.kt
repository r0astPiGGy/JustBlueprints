package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val path: String,
    val name: String
)