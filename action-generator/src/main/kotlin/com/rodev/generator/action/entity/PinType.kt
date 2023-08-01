package com.rodev.generator.action.entity

import kotlinx.serialization.Serializable

@Serializable
data class PinType(
    val id: String,
    val color: Int
)