package com.rodev.generator.action.entity

import com.rodev.generator.action.entity.extra_data.ExtraData
import kotlinx.serialization.Serializable

@Serializable
data class NodeModel(
    val id: String,
    val type: String,
    val input: List<PinModel>,
    val output: List<PinModel>,
    val extra: ExtraData? = null
)