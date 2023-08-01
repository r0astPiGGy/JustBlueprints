package com.rodev.jmcc_extractor.entity

import kotlinx.serialization.Serializable

@Serializable
data class EventData(
    val id: String,
    val cancellable: Boolean = false,
    val category: String? = null,
)
