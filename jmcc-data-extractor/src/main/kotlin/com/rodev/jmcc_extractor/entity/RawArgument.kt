package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class RawArgument(
    val id: String,
    val plural: Boolean,
    val type: String,
    val elementType: String? = null,
    val keyType: String? = null,
    val valueType: String? = null
)