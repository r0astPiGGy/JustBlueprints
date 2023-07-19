package com.rodev.nodeui.model

import kotlinx.serialization.Serializable

@Serializable
data class Pin(
    val uniqueId: String,
    val typeId: String,
    val value: String?,
)
