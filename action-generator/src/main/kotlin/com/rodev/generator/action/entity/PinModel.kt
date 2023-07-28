package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.extra_data.ExtraData
import kotlinx.serialization.Serializable

@Serializable
data class PinModel(
    val id: String,
    val type: String,
    val label: String = "",
    val extra: ExtraData? = null
)
