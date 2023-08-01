package com.rodev.generator.action.entity.extra_data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("list")
@Serializable
data class ListExtraData(
    val elementType: String
) : ExtraData()
