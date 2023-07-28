package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class Argument(
    val type: String? = null,
    val name: String? = null,
    val array: Boolean? = null,
    val length: Int? = null,
    val `enum`: List<String>? = null,
    val defaultBooleanValue: Boolean? = null
)