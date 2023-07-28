package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class ActionData(
    val id: String,
    val name: String,
    val `object`: String,
    val args: List<Argument>,
    val origin: String? = null,
    val assigning: String? = null,
    val containing: String? = null,
    val lambda: List<String>? = null,
    val conditional: Boolean? = null,
)


