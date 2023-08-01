package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("dictionary")
@Serializable
data class DictionaryExtraData(
    val keyType: String,
    val elementType: String
) : ExtraData()
