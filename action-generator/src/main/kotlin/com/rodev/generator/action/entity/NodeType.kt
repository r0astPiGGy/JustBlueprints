package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class NodeType(
    val id: String,
    val color: Int
)