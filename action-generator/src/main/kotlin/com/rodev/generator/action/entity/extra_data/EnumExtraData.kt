package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("enum")
@Serializable
data class EnumExtraData(
    val values: List<EnumEntry>
) : ExtraData()

@Serializable
data class EnumEntry(
    val id: String,
    val name: String
)