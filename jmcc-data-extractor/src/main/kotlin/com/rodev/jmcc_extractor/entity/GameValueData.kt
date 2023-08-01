package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class GameValueData(
    val id: String,
    val type: String,
    val worksWith: List<String>? = null,
    val keyType: String? = null,
    val valueType: String? = null,
    val elementType: String? = null
)
